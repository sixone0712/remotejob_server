if exist build_front (
	echo exist 'build_front'
) else (
	echo not exist 'build_front'
	mkdir build_front
)

cd build_front
if exist log-manager-front (
	echo exist 'log-manager-front'
) else (
	echo not exist 'log-manager-front'
	cmd /c git clone http://10.1.9.22:9080/product/log-manager-front.git
)

cd log-manager-front
cmd /c git remote update
cmd /c git pull origin





