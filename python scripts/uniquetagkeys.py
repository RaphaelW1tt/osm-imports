import matplotlib.pyplot as plt
import sys
import locale
locale.setlocale(locale.LC_ALL, '')

DOCUMENT_NAME = sys.argv[1]

def extractData(data_input):
    date = []
    count =  []
    tags = []

    for element in data_input:
        dat, cnt, tag = element.split(" ")
        dat = dat[:10]
        cnt = cnt.strip()
        cnt = float(cnt)
        date.append(dat)
        tags.append(int(tag))
        count.append(cnt)

    return date,count,tags

if DOCUMENT_NAME != "":
    with open(DOCUMENT_NAME, "r") as f:
        t = f.readlines()

date,count, tags = extractData(t)

fig, ax2 = plt.subplots(figsize=(12,7))
plt.xlabel("Time period (Days)")
color = 'slategray'
ax2.set_ylabel('Element count', color=color)
ax2.bar(date, count, color=color,zorder=0, width=1.0)
ax2.tick_params(axis='y', labelcolor=color)
# ax2.set_ylim(ymax = 9400000, ymin = 9200000) # noimport netherlands
# ax2.set_ylim(ymax = 1400000, ymin = 1050000) # noimport india
# ax2.set_yscale('log')
ax2.set_yticklabels(['{0:,}'.format(int(x)).replace(",", ".") for x in ax2.get_yticks().tolist()])

ax1 = ax2.twinx()

color = 'k'
ax1.set_ylabel('Unique tag key count', color=color)
ax1.plot(date, tags, color=color,zorder=10, linewidth=2.5)
ax1.tick_params(axis='y', labelcolor=color)

plt.xticks([date[0], date[len(date)/4], date[len(date)/2], date[len(date)-(len(date)/4)], date[-1]], visible=True, rotation="horizontal")
plt.show()