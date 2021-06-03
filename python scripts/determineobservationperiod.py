import sys

DOCUMENT_NAME = sys.argv[1]

MIN_MARGIN = 0.1

MAX_MARGIN = 0.9

def extractData(data_input):
    sum = 0
    for element in data_input:
        dat, ele = element.split(" ")
        ele = ele.strip()
        sum += int(ele)
    return sum

def extractDate(data_input, bench):
    sum = 0
    for element in data_input:
        dat, ele = element.split(" ")
        ele = ele.strip()
        sum += int(ele)
        if sum >= bench:
            print(dat)
            return
    return dat

if DOCUMENT_NAME != "":
    with open(DOCUMENT_NAME, "r") as f:
        t = f.readlines()

    creations = extractData(t)

    print(creations*MIN_MARGIN)
    print(creations*MAX_MARGIN)

    c = extractDate(t, MIN_MARGIN*(creations))

    c = extractDate(t, MAX_MARGIN*(creations))


