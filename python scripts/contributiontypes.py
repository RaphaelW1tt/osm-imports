import matplotlib.pyplot as plt
import sys
import pandas as pd
import numpy as np
from scipy.signal import find_peaks

DOCUMENT_NAME = sys.argv[1]

# start values of the element count
AND = 61719
threed = 4996016
bag = 10434087
ned_noimport = 9244276
ind_noimport = 1100214
AND_ind = 20170
PGS = 20170
building = 2866402
buildingphase1 = 2866402
buildingphase2 = 6751779
buildingphase3 = 8512200

last_element = None
last_element_columns = []

def extractData(data_input):
    date = []
    elements =  []

    for element in data_input:
        dat, ele = element.split(";")
        dat = dat[:10]
        ele = ele.strip()
        date.append(dat)
        elements.append(ele)

    return date,elements

if DOCUMENT_NAME != "":
    with open(DOCUMENT_NAME, "r") as f:
        t = f.readlines()

date,elements = extractData(t)

last_element = elements[-1]
for x in last_element[:-1].split("/"):
    bezeichner, count = x.split("=")
    last_element_columns.append(bezeichner)

elements_new = []
elements_without_count = []
elements_combined = []
elements_creat_del = []

for i in range(6):
    elements_without_count.append(0)
    elements_creat_del.append(0)

elements_new = ["[]","[TAG_CHANGE, GEOMETRY_CHANGE]","[TAG_CHANGE]","[GEOMETRY_CHANGE]","[DELETION]","[CREATION]"]

for i in range(len(elements)):
    z = elements[i][:-1].split("/")
    for l in z:
        if len(z) >= 1:
            if l != "":
                zz,count = l.split("=")
                for c in range(len(elements_new)):
                    if zz == elements_new[c]:
                        elements_without_count[c]=(int(count))

    elements_creat_del = []
    elements_combined.append(elements_without_count)
    elements_without_count =  []
    for trz in range(6):
        elements_without_count.append(0)

dataset1 = np.array([x[0] for x in elements_combined])

width = 0.4
ind = np.arange(len(dataset1))

creationsdeletions = [x[-2:] for x in elements_combined]
elements_combined = [x[1:-2] for x in elements_combined]
creationsdeletionsresult = [a - b for b,a in creationsdeletions]
data = [creationsdeletionsresult[0]]
for i in range(1,len(creationsdeletionsresult)):
    data.append(creationsdeletionsresult[i] + data[i-1])

elements_summarized = [a + b + c  for a,b,c in elements_combined]

creationsdeletionsresult = [x + AND for x in data] # specify start value of element count

fig, axes = plt.subplots(squeeze=False, figsize=((12,7)))
plt.subplots_adjust(left=None, bottom=None, right=None, top=None, wspace=None, hspace=0.0005)

df2 = pd.DataFrame(creationsdeletionsresult,index=date)
a2 = df2.plot(ax=axes[0,0], color="slategray", kind="bar", width=1.0)
# a2.set_ylim(ymax = 9400000, ymin = 9200000) # noimport netherlands
# a2.set_ylim(ymax = 1400000, ymin = 1050000) # noimport india

a2.set_yticklabels(['{0:,}'.format(int(x)).replace(",", ".") for x in a2.get_yticks().tolist()], color="slategray")
a2.get_legend().remove()
a2.set_ylabel('Element count', color="slategray")
a2.set_xlabel("Time period (Weeks)")

df = pd.DataFrame(elements_combined,index=date)
df2 = pd.DataFrame(elements_summarized,index=date)
x = np.array(elements_summarized)
peaks2, prop = find_peaks(x, height=np.mean(x)*3)
ax1 = df.plot(ax=axes[0,0],secondary_y=True, legend=False, xticks=[0, len(date)/4, len(date)/2, len(date)-(len(date)/4), len(date)-1], rot=0,linewidth=2.5)
ax1 = df2.plot(ax=axes[0,0],secondary_y=True, legend=False, xticks=[0, len(date)/4, len(date)/2, len(date)-(len(date)/4), len(date)-1],rot=0,color="k", label="Sum",linestyle="--")
ax1.plot(peaks2,x[peaks2], "ro")
ax1.set_ylabel('Contribution type count')
ax1.set_yticklabels(['{0:,}'.format(int(x)).replace(",", ".") for x in ax1.get_yticks().tolist()])
box = ax1.get_position()
ax1.set_position([box.x0, box.y0 + box.height * 0.1,
                 box.width, box.height * 0.9])
ax1.legend(loc='upper center', labels= ["[TAG_CHANGE, GEOMETRY_CHANGE]","[TAG_CHANGE]","[GEOMETRY_CHANGE]","Sum", "Peak"], bbox_to_anchor=(0.5, 1.12),
           fancybox=False, shadow=False, ncol=3)
ax1.set_yscale('symlog')

plt.show()


# Result plots

# importsN = ["AND", "3dShapes", "BAG", "no import"]
# importsI = ["PGS", "AND", "building", "building phase 1","building phase 2","building phase 3", "no import"]
#
# countsN = [5,1,3,2]
# countsI = [3,3,7,3,1,2,6]
#
#
# fig, (ax1, ax2) = plt.subplots(1, 2, figsize=((12,7)))
# ax1.bar(importsN, countsN)
# ax1.get_children()[3].set_color('darkblue')
# ax1.set(ylabel="Number of peaks")
# ax1.set_title("Netherlands")
# ax2.bar(importsI, countsI)
# ax2.get_children()[6].set_color('darkblue')
# ax2.set(ylabel="Number of peaks")
# ax2.set_title("India")
# plt.xticks(rotation=45)
#
# plt.show()
