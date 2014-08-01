/*
* Mobiscroll module for xml-forms plugin
*
* Grails xml-forms plug-in
* Copyright 2013 Open-T B.V., and individual contributors as indicated
* by the @author tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Affero General Public License
* version 3 published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.

* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see http://www.gnu.org/licenses
*/

$(function() {

    var defaults = window.dialog.messages.mobiscroll.regional;
    defaults.animate = 'pop';
    defaults.display = 'bubble';
    defaults.scrollLock = false; //turned off because of issues within iframes

    if (navigator.userAgent.match(/Android/i)) {
        if (navigator.userAgent.match(/Android 4/i)) {
            defaults.theme = "android-holo";
        }
        else {
            defaults.theme = "android";
        }
    }
    else if (navigator.userAgent.match(/iPad|iPod|iPhone/i)) {
        if (navigator.userAgent.match(/CPU OS 7_/i)) {
            defaults.theme = "ios7";
        }
        else {
            defaults.theme = "ios";
        }
    }

    $.mobiscroll.setDefaults(defaults);

});
