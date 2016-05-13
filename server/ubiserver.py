from flask import Flask
from flask import request
from flask import g
from flask import jsonify
from threading import Lock
from flask import abort

app = Flask(__name__)

class DataStore(object):
  def __init__(self):
    self.lock = Lock()
    self.count = 0
    self.ubibikers = {}
    self.stations = {}
    self.queue = []
   
ds = DataStore() 

class Station():
  def __init__ (self, name, location, bikes):
    self.name = name
    self.location = location
    self.bikes = bikes

  def serialize(self):
    return {
      "name": self.name,
      "location": self.location,
      "bikes": [obj.serialize() for obj in self.bikes]
    }

class Bike():
  def __init__ (self, bike, state):
    self.bike = bike
    self.state = state
    self.owner = None
    self.station = None

  def serialize(self):
    return {
      "state": self.state,
      "bike": self.bike
    }

  def teste(self):
    return {
      "state": self.state,
      "bike": self.bike,
      "owner": self.owner,
      "station": self.station
    }


class Populate():
  def __init__(self):
    #Estacoes
    bike1 = Bike("1","station")
    bike2 = Bike("2","station")
    bike3 = Bike("3","station")
    bike4 = Bike("4","station")
    bikes = [bike1, bike2, bike3, bike4]
    ds.stations["Station 1"] = Station("Station 1", {"lat": "38.7059084","lng": "-9.144370200000026"}, bikes)
    
    bike5 = Bike("5","station")
    bike6 = Bike("6","station")
    bikes = [bike5, bike6]
    ds.stations["Station 2"] = Station("Station 2", {"lat": "38.710672","lng": "-9.139091000000008"}, bikes)
   
    bike7 = Bike("7","station")
    bike8 = Bike("8","station")
    bike9 = Bike("9","station")
    bikes = [bike7, bike8, bike9]
    ds.stations["Station 3"] = Station("Station 3", {"lat": "38.742599","lng": "-9.133806999999933"}, bikes)
    
    bike10 = Bike("10","station")
    bikes = [bike10]
    ds.stations["Station 4"] = Station("Station 4", {"lat": "38.7368192","lng": "-9.138704999999959"}, bikes)
    
    ds.stations["Station 5"] = Station("Station 5", {"lat": "38.7068223","lng": "-9.133050099999991"}, [])

Populate()

class Ubibiker():
  def __init__ (self, name, email, password):
    self.name = name
    self.email = email
    self.password = password
    self.points = 0
    self.tracks = []
  
  def serialize(self):
    return {
      "name": self.name,
      "email": self.email,
      "points": self.points,
      "tracks": [obj.serialize() for obj in self.tracks]
    }

  def serialize_profile(self):
    return {
      "name": self.name,
      "email": self.email,
      "points": self.points
    }

  def serialize_search(self):
    return {
      "name": self.name,
      "email": self.email
    }

class Trajectory():
  def __init__(self, name, ts, start, end, line):
    self.name = name
    self.ts = ts
    self.start = start
    self.end = end
    self.line = line

  def serialize(self):
    return {
      "name": self.name,
      "ts": self.ts,
      "start": self.start,
      "end": self.end,
      "line": self.line
    }

@app.route("/")
def hello():
  return "Hello World!"

@app.route("/test", methods=["POST"])
def test():
  line = [{"lat": 94.2023,"lng": 93.21231},{"lat": 94.2023,"lng": 93.21231}, {"lat": 94.2023,"lng": 93.21231}, {"lat": 94.2023,"lng": 93.21231}]
  track1 = Trajectory("Tecnico","21949384938", {"lat": 94.2023,"lng": 93.21231}, {"lat": 94.2023,"lng": 93.21231}, line)
  track2 = Trajectory("Casa","21949384938", {"lat": 94.2023,"lng": 93.21231}, {"lat": 94.2023,"lng": 93.21231}, line)
  ubibiker = Ubibiker("Diogo Andrade","diogo@yubo.be", "password")
  ubibiker.tracks.append(track1)
  ubibiker.tracks.append(track2)
  ds.ubibikers[ubibiker.email] = ubibiker  
  json = request.get_json(force=True)
  print(json)
  return jsonify(ubibiker.serialize())

@app.route("/book")
def book():
  if 'email' in request.args and\
     'station' in request.args:
    email = request.args.get('email')
    name = request.args.get('station')
    station = ds.stations[name]
    if not station.bikes:
      return abort(410) # Resource not available
    else:
      with ds.lock:
        bike = station.bikes.pop()
      bike.state = "B"
      bike.owner = email
      bike.station = name
      with ds.lock:
        ds.queue.append(bike) 
      return bike.bike
  else:
    abort(400)

@app.route("/book/check")
def book_check():
  if 'email' in request.args:
    email = request.args.get('email')
    for bike in ds.queue:
      if bike.owner == email and bike.state == "B":
        return bike.station
    return ""
  else:
    abort(400)

@app.route("/book/cancel")
def book_cancel():
  if 'email' in request.args:
    email = request.args.get('email')
    station = request.args.get('station')
    for bike in ds.queue:
      if bike.owner == email and bike.state == "B" and bike.station == station:
          station_name = bike.station
          bike.owner = None
          bike.station = None
          bike.state = "station"
          with ds.lock:
            ds.stations[station_name].bikes.append(bike)
            ds.queue.remove(bike)
            return "Book canceled"
    abort(410) # No bike booked
  else:
    abort(400)


@app.route("/stations")
def stations():
  return jsonify(stations=[v.serialize() for k, v in ds.stations.iteritems()])

@app.route("/profile")
def profile():
  if 'email' in request.args:
    email = request.args.get('email')
    ubibiker = ds.ubibikers[email]
    return jsonify(ubibiker.serialize())
  else:
    abort(400)

@app.route("/login")
def login():
  if 'email' in request.args and\
     'password' in request.args:
    email = request.args.get('email')
    password = request.args.get('password')
    if email in ds.ubibikers:
      if ds.ubibikers[email].password == password:
        ubibiker = ds.ubibikers[email]
        return jsonify(ubibiker.serialize_profile())
      else:
        abort(401) # wrong password
    else:
      abort(404) # User does not exist.
  else:
    abort(400) # Missing fields.

@app.route("/register")
def register():
  if 'name' in request.args and\
     'password' in request.args and\
     'email' in request.args:
    name = request.args.get('name')
    password = request.args.get('password')
    email = request.args.get('email')
    if email not in ds.ubibikers:
      ubibiker = Ubibiker(name, email, password)
      with ds.lock:
        ds.ubibikers[email] = ubibiker
      return jsonify(ubibiker.serialize())
    else:
      abort(409) # Email already exists.
  else:
    abort(400) # Missing fields.
  
@app.route("/ubibiker")
def ubibiker():
  def filter_users_by_name(name):
    return lambda (email, ubibiker): \
      all(map(lambda n: n in ubibiker.name.split(), name.split()))
    
  if 'name' in request.args:
    name = request.args.get('name')
    results = filter(filter_users_by_name(name), ds.ubibikers.iteritems())
 
    return jsonify(ubibikers=[v.serialize_search() for k, v in results])
  else:
    abort(400) # Missing fields.

@app.route("/bike/update", methods=["POST"])
def update_bike():
  json = request.get_json(force=True)

  return jsonify(json)

if __name__ == "__main__":
    app.run()
