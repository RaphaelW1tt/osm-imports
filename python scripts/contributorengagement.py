import matplotlib.pyplot as plt
import sys
import numpy as np
from matplotlib.ticker import MaxNLocator

DOCUMENT_NAME = sys.argv[1]
DOCUMENT_NAME2 = sys.argv[2]
DOCUMENT_NAME3 = sys.argv[3]

def extractData(data_input):
    user = []
    data = []
    count = []

    for element in data_input:
        #print(element)
        data = element.split(" ")

    for element in data:
        usr, cnt = element.split("=")
        cnt = cnt.strip()
        cnt = float(cnt)
        count.append(cnt)

    for element in data:
        usr, cnt = element.split("=")
        usr = usr.strip()
        user.append(usr)

    print("Number of users active in time period: " + str(len(user)))
    return user

def extractDataToMap(data_input):
    dic = {}
    data = []

    for element in data_input:
        data = element.split(" ")

    for element in data:
            usr, cnt = element.split("=")
            if usr not in dic.keys():
                dic[usr] = 0
    return dic


if DOCUMENT_NAME != "":
    with open(DOCUMENT_NAME, "r") as f:
        t = f.readlines()

usersDuringImport = extractData(t)

if DOCUMENT_NAME2 != "":
    with open(DOCUMENT_NAME2, "r") as f:
        t2 = f.readlines()

    usersBefore = extractDataToMap(t2)

    with open(DOCUMENT_NAME3, "r") as f:
        t3 = f.readlines()

    usersAfter = extractDataToMap(t3)

    count = 0
    csum = 0

    for i in range(len(usersDuringImport)):
        #if usersDuringImport[i] in usersBefore.keys(): # PRE-EXISTING
        if usersDuringImport[i] not in usersBefore.keys(): # IMPORT-INSPIRED
            count += 1
    for i in range(len(usersDuringImport)):
        #if usersDuringImport[i] in usersBefore.keys() and usersDuringImport[i] in usersAfter.keys(): # PRE-EXISTING
        if usersDuringImport[i] not in usersBefore.keys() and usersDuringImport[i] in usersAfter.keys(): # IMPORT-INSPIRED
            csum += 1

    print(csum)
    print(count)
    print("=")
    print((float(csum)/count)*100)


# # PRE-EXISTING
# n1 = [13.8888888889,22.1183800623,39.2233009709,19.285042333]
# i1 = [0,25,2.98972853999,19.1374663073,22.8426395939,3.20236813778,16.2561576355]
# n2 = [69.4444444444,62.6168224299,60.0,44.0263405456]
# i2 = [50,50,18.4426229508,41.2398921833,45.1776649746,24.7566063978,43.842364532]
#
# # IMPORT-INSPIRED
# n1novice = [12.5,10.7876712329,6.21359223301,4.59290187891]
# i1novice = [0,0,2.98972853999,1.74545454545,1.98675496689,3.20236813778,2.04572803851]
# n2novice = [23.6111111111,34.2465753425,15.9223300971,12.5260960334]
# i2novice = [0,14.2857142857,18.4426229508,6.61818181818,7.45033112583,24.7566063978,8.30324909747]
#
# # # TOTAL NUMBERS
# # n1 = [33.33333333333333,35.469613259668506,50.0,42.52]
# # i1 = [40,22.22222222,6.29082158817,21.24856815578465,24.594257178526842,8.821003557845664,19.6324951644]
# # n2 = [66.66666666666666,64.5303867403315,50.0,57.48]
# # i2 = [60,77.777777777,93.7091784118,78.75143184421535,75.40574282147315,91.17899644215434,80.3675048356]
#
#
# n3 = ["AND","3dShapes","BAG","no import"]
# i3 = [ "PGS", "AND", "building", "building phase 1","building phase 2","building phase 3", "no import"]
#
# fig, ax = plt.subplots(figsize=(15,7))
# ax.set_ylabel('% of active contributors')
# plt.rc('legend', fontsize=9)
#
# width = 0.15
# ind = np.arange(4)
# #plt.bar(ind,i1, width, label="Pre-existing mapper", color="brown")
# #plt.bar(ind+width,i2, width, label="Import-inspired mapper", color="mediumblue")
#
# plt.bar(ind,n2, width, label="After 1 year (pre-existing mapper)", color="brown")
# plt.bar(ind+width,n1, width, label="After more than 1 year (pre-existing mapper)", color="lightcoral")
#
# plt.bar(ind+width+width,n2novice, width, label="After 1 year (import-inspired mapper)", color="mediumblue")
# plt.bar(ind+width+width+width,n1novice, width, label="After more than 1 year (import-inspired mapper)", color="cornflowerblue")
#
# plt.legend(loc="best")
# plt.xticks(ind  + width + width  / 2, n3)
# plt.show()
