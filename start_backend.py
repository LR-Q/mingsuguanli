import subprocess 
subprocess.Popen('mvn spring-boot:run -Dspring-boot.run.profiles=dev',cwd=r'E:\CursorProject\LRminsu\yxly-backend',stdout=open(r'E:\CursorProject\LRminsu\backend-dev.log','w'),stderr=subprocess.STDOUT,shell=True) 
