import urllib

def ping(address):
    try:
        response = urllib.urlopen(address).getcode()
    except IOError:
        response = 0
    return response

if __name__ == "__main__":
    print(ping('http://www.stackoverflow.com'))