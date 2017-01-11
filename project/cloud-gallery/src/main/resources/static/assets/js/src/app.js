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
            vm.apps = new NgTableParams(
                {
                    count: 5
                },
                {
                    getData: search,
                    counts: [5, 10, 25],
                    paginationMinBlocks: 1,
                    paginationMaxBlocks: 5
                }
            );
            vm.app = {id: undefined, name: ''};
            vm.apps.metadata = undefined;
        }

        function save(app) {
            $http.post(APP, app)
                .then(response => app.id = response.data)
                .then(refresh)
                .catch(onError);
        }

        function update(app) {
            $http.put(APP, app)
                .then(refresh)
                .catch(onError);
        }

        function remove(app) {
            $http.delete(APP + app.id)
                .then(refresh)
                .catch(onError);
        }

        function search(params) {
            var page = params.page() - 1;
            var size = params.count();

            return $http.get(APP + 'search', {params: {page: page, size: size, sort: "date,DESC"}})
                .then(response => {
                    var data = response.data;
                    updateMetadata(data);
                    params.total(data.totalElements);
                    return data.content;
                })
                .catch(onError);
        }

        function updateMetadata(data) {
            if (data.content.length == 0) return;

            vm.apps.metadata = data.content[0].metadata;
        }

        /* DEBUG */
        function findAll() {
            return $http.get(APP)
                .then(response => response.data)
                .catch(onError);
        }

        /* TODO: delete single element of last page */
        function refresh() {
            vm.app = {id: undefined, name: ''};
            vm.apps.reload();
        }

        function onError(error) {
            var msg = '[message=\'' + error.data.message + '\']';
            alert(error.data.error + msg);
        }
    }
})();