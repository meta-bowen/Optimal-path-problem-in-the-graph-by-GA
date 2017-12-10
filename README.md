# GA-tsp--Cycling-route-problem
通过GA算法实现图中的最优路径问题。现实中的实际应用，通过选择三亚的20个适合骑行的自然景观经纬度点，利用该算法经过设定好的迭代次数得到一个近似最优解。特点：通过经纬度计算两点间距离（适用于地球曲面，但将地球看作球体进行求解），并引入精确计算包进一步减少误差值。