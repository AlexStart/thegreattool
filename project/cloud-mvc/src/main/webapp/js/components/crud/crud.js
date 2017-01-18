(function () {
    'use strict';

    var ngApp = angular.module('ngApp', ['ngTable', 'angular-loading-bar']);

    var config = {
        context: getContext(),
        component: {path: getComponentPath()}
    };


    ngApp.config(loadingBarConfig);

    loadingBarConfig.$inject = ['cfpLoadingBarProvider'];

    function loadingBarConfig(cfpLoadingBarProvider) {
        cfpLoadingBarProvider.includeBar = true;
        cfpLoadingBarProvider.includeSpinner = true;
        cfpLoadingBarProvider.latencyThreshold = 100;
        cfpLoadingBarProvider.spinnerTemplate = '<div class="loading-bar-spinner"><div class="loading-bar-container"><i class="fa fa-cog fa-spin fa-3x fa-fw"></i><\/div><\/div>';
    }


    ngApp.factory('translationService', translationService);

    translationService.$inject = ['$http', '$cacheFactory'];

    //TODO: change getting of context path
    function translationService($http, $cacheFactory) {
        var cache = $cacheFactory('translations');

        return {
            translate: translate,
            getCached: getCached
        };

        function translate(keys) {
            return $http.get(config.context + 'translations', {params: eval(keys)})
                .then(response => response.data)
                .catch(onError);
        }

        function getCached(key) {
            if (!cache.get(key)) cache.put(key, '');

            var value = cache.get(key);

            if (!value.length) {
                cache.put(key, ' ');

                $http.get(config.context + 'translations', {params: {keys: key}})
                    .then(response => {
                        cache.put(key, response.data[key]);
                    })
                    .catch(onError);
            }
            return value;
        }
    }


    ngApp.factory('crudService', crudService);

    crudService.$inject = ['$http', '$q'];

    function crudService($http, $q) {
        var api = null;

        return {
            save: save,
            remove: remove,
            update: update,
            search: search,
            findAll: findAll,
            configure: configure,
        };

        function configure() {
            return $http.get(config.component.path + 'crud.json').then(response => {
                var data = response.data;
                api = config.context + data.url + '/';
                return data;
            });
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
            if (!api) return $q(resolve => resolve([]));

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

    crudCtrl.$inject = ['NgTableParams', 'crudService', 'translationService'];

    function crudCtrl(NgTableParams, crudService, translationService) {
        var vm = this;

        vm.save = save;
        vm.reset = reset;
        vm.select = select;
        vm.remove = remove;
        vm.update = update;
        vm.status = status;
        vm.refresh = refresh;

        vm.$onInit = () => {
            crudService
                .configure()
                .then(data => {
                    translationService
                        .translate(data.custom.buttons)
                        .then(buttons => vm.buttons = buttons);
                })
                .then(refresh);
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
            vm.items.metadata = undefined;
            vm.item = {id: undefined, name: ''};
            vm.buttons = {};
        }

        function select(item) {
            vm.item = angular.copy(item);
        }

        function status(item) {
            if (!item.data.disabled) return '';
            return translationService.getCached('state.disabled');
        }

        function save(item) {
            crudService
                .save(item)
                .then(refreshAll)
                .catch(onError);
        }

        function update(item) {
            crudService
                .update(item)
                .then(refreshAll)
                .catch(onError);
        }

        function remove(item) {
            crudService
                .remove(item)
                .then(refreshAll)
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

        function refresh() {
            vm.items.reload();
        }

        function refreshAll() {
            reset();

            /* The table has previous state in this place,
               if there's single item & current page isn't first - manual page changing.
             */
            if (vm.items.data.length == 1) {
                var currPage = vm.items.page();
                if (currPage > 0) {
                    vm.items.page(currPage - 1);
                }
            } else {
                vm.items.reload();
            }
        }

        function reset() {
            vm.item = {id: undefined, name: ''};
        }
    }


    ngApp.component('crud', {
        templateUrl: config.component.path + 'crud.html',
        controller: crudCtrl,
        controllerAs: 'vm',
        bindings: {}
    });

    /*DEPRECATED, TODO: It's not Angular way!!!*/

    function getContext() {
        var path = window.location.href;
        return path.substring(0, path.lastIndexOf("/") + 1);
    }

    function getComponentPath() {
        var scripts = document.getElementsByTagName("script");
        var last = scripts[scripts.length - 1].src;
        return last.substring(0, last.lastIndexOf('/') + 1);
    }

    function onError(error) {
        if (error.data) {
            var msg = '[message=\'' + error.data.message + '\']';
            alert(error.data.error + msg);
        } else {
            alert(error);
        }
    }
})();