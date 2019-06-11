from bs4 import BeautifulSoup
import sys
import re

with open ("document.xml") as fp:
    soup = BeautifulSoup(fp, features="lxml")

#fp = sys.argv[1]
#handler = open(fp).read()

#soup = BeautifulSoup(fp)
#print(soup.name)
#finding tags names
#for tag in soup.find_all(re.compile("title")):
#    print(tag)
#finding strings inside of tags
for tag in soup.find_all(string=re.compile("nur")):
    print(tag)

#soup.find(string=re.compile("Test"))
#print(soup("w:b"))

for tag in soup.find_all("w:t"):
    print(tag)
