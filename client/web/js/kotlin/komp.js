if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'komp'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'komp'.");
}
if (typeof this['kotlinx-html-js'] === 'undefined') {
  throw new Error("Error loading module 'komp'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'komp'.");
}
var komp = function (_, Kotlin, $module$kotlinx_html_js) {
  'use strict';
  var TagConsumer = $module$kotlinx_html_js.kotlinx.html.TagConsumer;
  var get_create = $module$kotlinx_html_js.kotlinx.html.dom.get_create_4wc2mh$;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  function include($receiver, component) {
    var tmp$;
    var result = component.render_q0cphf$(Kotlin.isType(tmp$ = $receiver.consumer, TagConsumer) ? tmp$ : Kotlin.throwCCE());
    component.element = result;
    Komp_getInstance().define_eho435$(result, component);
  }
  function HtmlComponent() {
    this.element = null;
  }
  HtmlComponent.prototype.create = function () {
    var elem = this.element;
    if (elem != null) {
      Komp_getInstance().remove_lt8gi4$(elem);
    }
    elem = this.render_q0cphf$(get_create(document));
    Komp_getInstance().define_eho435$(elem, this);
    this.element = elem;
    return elem;
  };
  HtmlComponent.prototype.refresh = function () {
    Komp_getInstance().refresh_y4uc7f$(this.element);
  };
  HtmlComponent.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'HtmlComponent',
    interfaces: []
  };
  function Komp() {
    Komp_instance = this;
    this.elements_0 = HashMap_init();
  }
  Komp.prototype.define_eho435$ = function (element, component) {
    this.elements_0.put_xwzc9p$(element, component);
  };
  Komp.prototype.create_og2ns8$ = function (parent, component, insertAsFirst) {
    if (insertAsFirst === void 0)
      insertAsFirst = false;
    var element = component.create();
    if (insertAsFirst && parent.childElementCount > 0) {
      parent.insertBefore(element, parent.firstChild);
    }
     else {
      parent.appendChild(element);
    }
    this.elements_0.put_xwzc9p$(element, component);
  };
  Komp.prototype.remove_lt8gi4$ = function (element) {
    this.elements_0.remove_11rb$(element);
  };
  Komp.prototype.remove = function (component) {
    var tmp$_0;
    tmp$_0 = this.elements_0.entries.iterator();
    while (tmp$_0.hasNext()) {
      var tmp$ = tmp$_0.next();
      var key = tmp$.key;
      var value = tmp$.value;
      if (Kotlin.equals(value, component)) {
        this.elements_0.remove_11rb$(key);
      }
    }
  };
  Komp.prototype.refresh_ap2bc9$ = function (component) {
    this.refresh_y4uc7f$(component.element);
  };
  Komp.prototype.refresh_y4uc7f$ = function (element) {
    if (element != null) {
      var comp = this.elements_0.get_11rb$(element);
      if (Kotlin.isType(element, HTMLElement) && comp != null) {
        var parent = element.parentElement;
        var newElement = comp.create();
        parent != null ? parent.replaceChild(newElement, element) : null;
      }
    }
  };
  Komp.$metadata$ = {
    kind: Kotlin.Kind.OBJECT,
    simpleName: 'Komp',
    interfaces: []
  };
  var Komp_instance = null;
  function Komp_getInstance() {
    if (Komp_instance === null) {
      new Komp();
    }
    return Komp_instance;
  }
  var package$nl = _.nl || (_.nl = {});
  var package$astraeus = package$nl.astraeus || (package$nl.astraeus = {});
  var package$komp = package$astraeus.komp || (package$astraeus.komp = {});
  package$komp.include_dqcce7$ = include;
  package$komp.HtmlComponent = HtmlComponent;
  Object.defineProperty(package$komp, 'Komp', {
    get: Komp_getInstance
  });
  Kotlin.defineModule('komp', _);
  return _;
}(typeof komp === 'undefined' ? {} : komp, kotlin, this['kotlinx-html-js']);

//@ sourceMappingURL=komp.js.map
