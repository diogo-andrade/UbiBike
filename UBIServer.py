#!/usr/bin/python
import socket, sys, os
from Ubibiker import Ubibiker


def getUbibikers():
    global ubibikers
    return ubibikers

ubibikers = {}

def setUbibikers(v):
    global ubibikers
    ubibikers = v

class UBIServer:

    def __init__(self):
        # Create a TCP/IP socket
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        # Bind the socket to the port
        server_address = (socket.gethostname(), 8081)
        print >>sys.stderr, 'starting up on %s port %s' % server_address
        self.sock.bind(server_address)
        self.listen()

    def listen(self):
        # Listen for incoming connections
        self.sock.listen(5)

        while True:
            # Wait for a connection
            print >>sys.stderr, 'waiting for a connection'
            self.connection, client_address = self.sock.accept()

            try:
                print >>sys.stderr, 'connection from', client_address
                new_pid = os.fork()
                
                if new_pid == 0:     # Child Process
                    # Receive the data in small chunks and retransmit it
                    self.data = self.connection.recv(6)
                    self.data = self.data.split()

                    if self.data[0] == "LOGIN":
                        print >>sys.stderr, 'Login'
                        self.login()
                    elif self.data[0] == "QUERY":  # Search Query
                        self.ubibikers()
                    elif self.data[0] == "REGIS":
                        self.register()
                    else:
                         print>>sys.stderr, "Nothing"

                    #self.connection.sendall('OK\n')
                    print>>sys.stderr, "enviei ok"

                    os._exit(0)
                else:   # Father Process
                    self.connection.close()
            finally: 
                # Clean up the connection
                self.connection.close()

    def login(self):
        # recebe a restante informacao
        message = ""
        while True:
            chunk = self.connection.recv(1024)
            message += chunk
            print >>sys.stderr, 'received "%s"' % message.replace("/EOM", "")
            if chunk == "" or "/EOM" in message: # End Of Message
                break

        self.connection.sendall('OK/EOM');
                        
        print>>sys.stderr, "Nothing"
        return None

    def ubibikers(self):
        return None

    def register(self):
        message = ""
        while True:
            chunk = self.connection.recv(1024)
            message += chunk
            if chunk == "" or "/EOM" in message: # End Of Message
                message = message.replace("/EOM","")
                print >>sys.stderr, '[Regiter] received "%s"' % message
                break

        request = message.split(" ")

        #Process data

        if not request[1] in getUbibikers().keys():
            ubibiker = Ubibiker(request[0], request[1], request[2])
            setUbibikers(getUbibikers().update({request[1]: ubibiker}))
            self.connection.sendall('OK/EOM')
            print >>sys.stderr, '[Regiter] Nao existe', request[0], request[1], request[2]
        else:
            self.connection.sendall('NOK/EOM')
            print >>sys.stderr, '[Regiter] existe'

        if request[1] in getUbibikers().keys():
            print(getUbibikers().get(request[1]).password)
        print >>sys.stderr, 'Tira', getUbibikers().get(request[1]).email
        print >>sys.stderr, 'Tira', getUbibikers().get("diogo@yubo.be").email
        print >>sys.stderr, 'Tira', getUbibikers().get("geral@desordem.org").email
        return None

setUbibikers({})

UBIServer()
