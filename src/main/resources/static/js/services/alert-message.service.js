angular.module('mainApp').service('AlertMessageService', function($rootScope, $translate) {
    var msgTypeClass = {success: 'alert alert-success', warning: 'alert alert-warning', error: 'alert alert-danger'};
    $rootScope.errMessage = null;

    function getMessageClass(messageType) {
        return msgTypeClass[messageType];
    }

    function displayMessage(message, messageType) {
        var messageClass = getMessageClass(messageType);

        $rootScope.errMessage = {
            str : message,
            classAtt : messageClass,
            type : messageType
        };
    }

    function translateKey(messageKey, replaceValues){
        return $translate.instant(messageKey, replaceValues);
    }

    this.displaySuccess = function(messageKey, replaceValues) {
        var message = translateKey(messageKey,replaceValues);
        displayMessage(message, 'success');
    }

    this.displayWarning = function(messageKey, replaceValues) {
        var message = translateKey(messageKey,replaceValues);
        displayMessage(message, 'warning');
    }

    this.displayError = function(messageKey, replaceValues) {
        var message = translateKey(messageKey,replaceValues);
        displayMessage(message, 'error');
    }

    this.removeMessage = function() {
        $rootScope.errMessage = null;
    }
    $rootScope.removeErrorMessage = this.removeMessage;
});