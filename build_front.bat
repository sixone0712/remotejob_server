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
cmd /c yarn install
cmd /c yarn build

cmd /c rmdir /S /Q ..\..\src\main\resources\static
cmd /c mkdir ..\..\src\main\resources\static
cmd /c xcopy /S /Y build ..\..\src\main\resources\static

cd ..
cd ..






