import glob
import os

files = glob.glob('*.*')


WINDOWS_LINE_ENDING = b'\r\n'
UNIX_LINE_ENDING = b'\n'

# relative or absolute file path, e.g.:

for file in files:
    if '.py' in file: continue
    with open(file, 'rb') as open_file:
        content = open_file.read()
    content = content.replace(WINDOWS_LINE_ENDING, UNIX_LINE_ENDING)
    with open(file, 'wb') as open_file:
        open_file.write(content)
