#!/bin/bash


moduleTemplate="angular.module('MODULE', []);"

controllerTemplate="angular.module('MODULE.controllers')\n
.controller('MODULECtrl', function (\$scope) {\n\n
});"

directiveTemplate="angular.module('MODULE.directives')\n
	.directive('MODULE', function () {\n
	return {\n
		restrict: 'E',\n
		templateUrl: 'MODULE/MODULE.html',\n
		controller: 'MODULECtrl'\n
	};\n
});"

serviceTemplate="angular.module('ngHuggin.social.services')\n
	.service('PostService', function () {\n\n
});"

for val in $(ls); 
do 
	echo "Generating: " $val;
	#echo $(sed -i "s/MODULE/$val/g" $controllerTemplate);
	#touch "$val"/"$val"-controller.js;
	#touch "$val"/"$val"-directive.js;
	#touch "$val"/"$val".js;
	
	# touch "$val"/"$val"{info,icon,form}.html;
	echo "<h1>$val Info</h1>" > "$val"/"$val"info.html;
	echo "<h1>$val Icon</h1>" > "$val"/"$val"icon.html;
	echo "<h1>$val Form</h1>" > "$val"/"$val"form.html;

	echo -e $moduleTemplate | sed "s/MODULE/$val/g" > "$val"/"$val".js;
	echo -e $controllerTemplate | sed "s/MODULE/$val/g" > "$val"/"$val"-controller.js;
	echo -e $directiveTemplate | sed "s/MODULE/$val/g" > "$val"/"$val"-directive.js;
	echo -e $serviceTemplate | sed "s/MODULE/$val/g" > "$val"/"$val"-service.js;
done