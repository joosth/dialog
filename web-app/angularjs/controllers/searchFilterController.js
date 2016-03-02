/**
 * Created by Max on 01-03-16.
 */

wfpForms.controller('searchFilterController', ['$scope', function($scope){
    $scope.searchFields = wfp.data.searchFields;
    $scope.selectedFields = [];

    var defaultFields = wfp.data.defaultSearchFields;

    console.log("searchFields", $scope.searchFields);


    angular.forEach(defaultFields, function(value, key){
       if ($scope.searchFields[value]){
           $scope.selectedFields.push($scope.searchFields[value]);
           $scope.searchFields[value].id = key;
       }
    });

    console.log("searchFields", $scope.searchFields);


    $scope.filterChange = function(newVal, oldVal){
        console.log("New", newVal);
        console.log("Old", oldVal);
    };

}]);