(function () {
    'use strict';

    var app = angular.module('app', ['ngTable']);

    app.controller('galleryCtrl', galleryCtrl);

    galleryCtrl.$inject = ['NgTableParams', '$http'];

    function galleryCtrl(NgTableParams, $http) {
        var vm = this;

        vm.save = save;
        vm.remove = remove;
        vm.update = update;
        vm.reset = refresh;

        var APP = '/apps/';

        activate();

        function activate() {
            vm.apps = new NgTableParams({}, {counts: [], getData: findAll});
            vm.app = {id: undefined, name: ''};
        }

        function save(app) {
            $http.post(APP, app)
                .then(response => app.id = response.data)
                .then(refresh);
        }

        function update(app) {
            $http.put(APP, app)
                .then(refresh);
        }

        function remove(app) {
            $http.delete(APP + app.id)
                .then(refresh);
        }

        function findAll() {
            return $http.get(APP)
                .then(response => response.data);
        }

        function refresh() {
            vm.app = {id: undefined, name: ''};
            vm.apps.reload();
        }
    }
})();