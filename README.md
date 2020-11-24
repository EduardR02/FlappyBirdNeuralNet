# Flappy Bird Neural Network
This requires the uploaded library and Maven, although I haven't tried it without I'm pretty sure it won't work  
You can control the speed of the Simulation with the slider or with "a" and "d"  
You can also load and save your models, which saves only the two best birds ("l" for load and "s" for save)  
By changing the variable loadSavedGen you can either load all birds or just 1, the best one that has been saved  
To do that you will need to press "l" while the Sim is running  
You can also toggle that setting by pressing "c"  
By pressing "r" you can restart  
This can all be modified in keyPressed()  
I am not entirely sure if Bird.mutate() is also crossover but probably not  
All other parameters of the Simulation like gravity, jump height or obstacle size and height can be modified in Bird and Obstacle  
