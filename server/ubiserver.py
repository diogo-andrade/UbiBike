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
   
ds = DataStore()    

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
      "tracks": self.tracks
    }

  def serialize_profile(self):
    return {
      "name": self.name,
      "email": self.email,
      "points": self.points
    }

@app.route("/")
def hello():
  return "Hello World!"


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
    #foundlist = [ubibiker for email, ubibiker in ds.ubibikers.iteritems() if name in ubibiker.name.split()]
    results = filter(filter_users_by_name(name), ds.ubibikers.iteritems())
    return jsonify(ubibikers=[v.serialize() for k, v in results])
  else:
    abort(400) # Missing fields.

@app.route("/bike/update", methods=["POST"])
def update_bike():
  json = request.get_json(force=True)

  return jsonify(json)

if __name__ == "__main__":
    app.run()
