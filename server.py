#!/usr/bin/python
import socket, sys, os

def login():
    return None

def ubibikers():
    return None

def main():
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to the port
    server_address = (socket.gethostname(), 8081)
    print >>sys.stderr, 'starting up on %s port %s' % server_address
    sock.bind(server_address)

    # Listen for incoming connections
    sock.listen(5)

    while True:
        # Wait for a connection
        print >>sys.stderr, 'waiting for a connection'
        connection, client_address = sock.accept()

        try:
            print >>sys.stderr, 'connection from', client_address
            new_pid = os.fork()
 
            if new_pid == 0:     # Child Process
                # Receive the data in small chunks and retransmit it
                data = connection.recv(16)
                data = data.split()

                if data[0] == "LOGIN":
                    login()  
                elif data[0] == "UBIBIKERS":
                    ubibikers()
                else:
                    print>>sys.stderr, "Nothing"

                while True:
                    data = connection.recv(16)
                    print >>sys.stderr, 'received "%s"' % data
                    if data:
                        print >>sys.stderr, 'sending data back to the client'
                        connection.sendall(data)
                    else:
                        print >>sys.stderr, 'no more data from', client_address
                        break
                os._exit(0)
            else:   # Father Process
                connection.close()
            
        finally:
            # Clean up the connection
            connection.close()


if __name__ == "__main__":
    main()

