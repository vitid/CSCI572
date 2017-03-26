import networkx as nx

G = nx.read_edgelist("edgeList.txt", create_using=nx.DiGraph())
page_rank = nx.pagerank(G,alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',dangling=None)

buffer = ""
for id in page_rank:
	buffer = buffer + id + " " + str(page_rank[id]) + "\n"

f = open("external_PageRankFile.txt","w")
f.write(buffer)