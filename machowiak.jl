


using Gadfly
using RDatasets
using DataFrames

x = readcsv("/home/pawelch/repos/studia/szereg/ugh.csv")
d = DataFrame(NAME = x[:,1], CPU = x[:,2], PROC = x[:,3], RESULT = x[:,4], TIME = x[:,5])


sm = d[d[:CPU] .== 3,:]

function normalize(src_d)
  sort!(src_d, cols=[:CPU, :PROC])
  d=copy(src_d)
  base = d[d[:NAME] .== "Lpt",:]
  println(base[:RESULT])
  rv = copy(src_d)
  for subdf in groupby(rv, :NAME)
    subdf[:RESULT] -= base[:RESULT][1:length(subdf[:RESULT])]
  end
  return rv[d[:NAME] .!= "Lpt", :]
end
nd=normalize(sm)

for subdf in groupby(nd, :NAME)
    println(subdf)
end
Gadfly.set_default_plot_size(21cm, 12cm)
plts=Dict()
plts[:all] = plot(sm, y="TIME", color="NAME", x= "PROC", Geom.line, Guide.xlabel("Ilość Procesów"), Guide.ylabel("Czas obliczeń (μs)"), Scale.y_sqrt(format=:plain), Geom.smooth,Guide.title("Dla 3 Procesorów"))

reduced_data=sm[((sm[:NAME] .== "Lpt") + (sm[:NAME] .== "Greedy")),:]

reduced_data=reduced_data[reduced_data[:TIME] .< 100,:]

plts[:reduced] = plot(reduced_data, y="TIME", color="NAME", x= "PROC", Geom.line, Guide.xlabel("Ilość Procesów"), Guide.ylabel("Czas obliczeń (μs)"), Geom.smooth,Guide.title("Dla 3 Procesorów"))


plts[:odchylenie_bar] = plot(nd, y="RESULT", color="NAME", x= "PROC", Geom.bar, Guide.xlabel("Ilość Procesów"), Guide.ylabel("Odchylenie względem LPT"), Geom.smooth(method=:loess, smoothing=0.9),Guide.title("Dla 3 Procesorów"))

plts[:dots_odchylenie] = plot(nd, y="RESULT", color="NAME", x= "PROC", Geom.point, Guide.xlabel("Ilość Procesów"), Guide.ylabel("Odchylenie względem LPT"), Guide.title("Dla 3 Procesorów"), Geom.smooth(method=:loess, smoothing=0.9))

plts[:hist] = plot(nd, x="RESULT", color="NAME", Geom.histogram,Guide.xlabel("Odchylenie względem LPT"))

for (k,v) in plts
  draw(PNG("3-$k.png",21cm, 12cm), v)
end
