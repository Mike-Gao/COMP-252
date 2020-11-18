def make_edge(G, node1, node2, w):
    if node1 not in G:
        G[node1] = {}
    if node2 not in G:
        G[node2] = {}
    if node2 not in G[node1]:
        (G[node1])[node2] = w
    return G

def bellmanFord(G,s,t):
    '''  make the reverse graph '''
    
    reverseG = {}
    for v in G:
        for w in G[v]:
            make_edge(reverseG, w, v, (G[v])[w])

    opt = {}
    for v in G:
        opt[v] = 10000    
    opt[s] = 0
    
    prev = {}
    
    for i in range(len(G)):
        for v in G:
            if v in reverseG:
                for w in reverseG[v]:
                    if opt[w] + (reverseG[v])[w] < opt[v]:
                        opt[v] = opt[w] + (reverseG[v])[w]
                        prev[v] = w    
    print "here are the lengths of the shortest paths: " 
    print opt
    print "\n and here is the path from " + s + " to each vertex " 

    for w in G:
        v = w
        path = " " + w
        while (v != s):
            path += " " + prev[v]
            v = prev[v]
        print path
    
def test():
    (a,b,c,d,e,f,g) = ('A', 'B', 'C', 'D', 'E', 'F', 'G')
    edges = ((a,b,4),(a,f,2),(b,c,3),(b,e,5),(c,d,1),(e,d,-8),(f,e,1), (c,f,-20))
    G = {}
    for (v,w,cost) in edges:
        make_edge(G, v, w, cost)
    
    bellmanFord(G, a, g)
    
test()
