
import os

top = "C:\\Users\\alexm\\projects\\Android\\YamcaApp\\app\\src\\main\\java\\alexman\\yamca_app"
for dirpath, dirnames, filenames in os.walk(top):
    for filename in filenames:
        filename = os.path.join(dirpath, filename)
        print(filename)
        with open(filename, "r") as file:
            contents = file.read()
        
        contents = contents.replace(" eventdeliverysystem", " alexman.yamca_app.eventdeliverysystem")

        with open(filename, "w") as file:
            file.write(contents)
        
        
        
