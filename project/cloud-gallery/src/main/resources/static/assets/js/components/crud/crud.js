(function () {
    'use strict';

    var ngApp = angular.module('ngApp', ['ngTable']);


    ngApp.factory('crudService', crudService);

    crudService.$inject = ['$http'];

    function crudService($http) {
        return {
            save: save,
            setAPI: setAPI,
            remove: remove,
            update: update,
            search: search,
            findAll: findAll
        };

        var api = null;

        function setAPI(url) {
            api = url;
        }

        function save(item) {
            return $http.post(api, item).then(response => item.id = response.data);
        }

        function update(item) {
            return $http.put(api, item);
        }

        function remove(item) {
            return $http.delete(api + item.id);
        }

        function search(params) {
            if (!api) return [];

            var page = params.page() - 1;
            var size = params.count();

            return $http.get(api + 'search', {params: {page: page, size: size, sort: "date,DESC"}})
                .then(response => {
                    var data = response.data;
                    params.total(data.totalElements);
                    return data.content;
                });
        }

        function findAll() {
            return $http.get(api).then(response => response.data);
        }
    }


    ngApp.controller('crudCtrl', crudCtrl);

    crudCtrl.$inject = ['NgTableParams', 'crudService'];

    function crudCtrl(NgTableParams, crudService) {
        var vm = this;

        vm.save = save;
        vm.remove = remove;
        vm.update = update;
        vm.reset = refresh;

        vm.$onInit = () => {
            crudService.setAPI(vm.api + '/');
            refresh();
        };

        activate();

        function activate() {
            vm.items = new NgTableParams(
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
            vm.item = {id: undefined, name: ''};
            vm.items.metadata = undefined;
        }

        function save(item) {
            crudService
                .save(item)
                .then(refresh)
                .catch(onError);
        }

        function update(item) {
            crudService
                .update(item)
                .then(refresh)
                .catch(onError);
        }

        function remove(item) {
            crudService
                .remove(item)
                .then(refresh)
                .catch(onError);
        }

        function search(params) {
            return crudService
                .search(params)
                .then(updateMetadata)
                .catch(onError);
        }

        function updateMetadata(data) {
            if (data.length == 0) return data;

            vm.items.metadata = data[0].metadata;
            return data;
        }

        /* TODO: delete single element of last page */
        function refresh() {
            vm.item = {id: undefined, name: ''};
            vm.items.reload();
        }

        function onError(error) {
            var msg = '[message=\'' + error.data.message + '\']';
            alert(error.data.error + msg);
        }
    }


    ngApp.component('crud', {
        templateUrl: getBasePath() + 'crud.html',
        controller: crudCtrl,
        controllerAs: 'vm',
        bindings: {
            api: '@'
        }
    });

    function getBasePath() {
        var scripts = document.getElementsByTagName("script");
        var last = scripts[scripts.length - 1].src;
        return last.substring(0, last.lastIndexOf('/') + 1);
    }
})();