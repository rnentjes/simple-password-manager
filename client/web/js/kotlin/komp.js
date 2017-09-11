if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'komp'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'komp'.");
}
if (typeof this['kotlinx-html-js'] === 'undefined') {
  throw new Error("Error loading module 'komp'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'komp'.");
}
var komp = function (_, Kotlin, $module$kotlinx_html_js) {
  'use strict';
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var IllegalStateException = Kotlin.kotlin.IllegalStateException;
  var TagConsumer = $module$kotlinx_html_js.kotlinx.html.TagConsumer;
  var get_create = $module$kotlinx_html_js.kotlinx.html.dom.get_create_4wc2mh$;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  function DomDiffer() {
    DomDiffer_instance = this;
  }
  DomDiffer.prototype.replaceDiff_26ucz2$ = function (newElement, oldElement) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    if (!newElement.isEqualNode(oldElement)) {
      this.replaceNode_0(newElement, oldElement);
      return newElement;
    }
     else {
      tmp$ = until(0, newElement.children.length);
      tmp$_0 = tmp$.first;
      tmp$_1 = tmp$.last;
      tmp$_2 = tmp$.step;
      for (var index = tmp$_0; index <= tmp$_1; index += tmp$_2) {
        var newChild = newElement.children[index];
        var oldChild = oldElement.children[index];
        if (Kotlin.isType(newChild, Element) && Kotlin.isType(oldChild, Element)) {
          this.replaceDiff_26ucz2$(newChild, oldChild);
        }
         else {
          throw new IllegalStateException('Children are not nodes! ' + Kotlin.toString(newChild) + ', ' + Kotlin.toString(oldChild));
        }
      }
      return oldElement;
    }
  };
  DomDiffer.prototype.replaceNode_0 = function (newElement, oldElement) {
    var tmp$;
    tmp$ = oldElement.parentElement;
    if (tmp$ == null) {
      throw new IllegalStateException('oldElement has no parent! ' + oldElement);
    }
    var parent = tmp$;
    parent.replaceChild(newElement, oldElement);
  };
  DomDiffer.$metadata$ = {
    kind: Kotlin.Kind.OBJECT,
    simpleName: 'DomDiffer',
    interfaces: []
  };
  var DomDiffer_instance = null;
  function DomDiffer_getInstance() {
    if (DomDiffer_instance === null) {
      new DomDiffer();
    }
    return DomDiffer_instance;
  }
  function include($receiver, component) {
    var tmp$;
    var result = component.render_q0cphf$(Kotlin.isType(tmp$ = $receiver.consumer, TagConsumer) ? tmp$ : Kotlin.throwCCE());
    component.element = result;
    Komponent$Companion_getInstance().define_63y3pe$(result, component);
  }
  function include_0($receiver, component) {
    var tmp$;
    var result = component.render_q0cphf$(Kotlin.isType(tmp$ = $receiver.consumer, TagConsumer) ? tmp$ : Kotlin.throwCCE());
    component.element = result;
    Komponent$Companion_getInstance().define_63y3pe$(result, component);
  }
  function include_1($receiver, component) {
    var tmp$;
    var result = component.render_q0cphf$(Kotlin.isType(tmp$ = $receiver.consumer, TagConsumer) ? tmp$ : Kotlin.throwCCE());
    component.element = result;
    Komponent$Companion_getInstance().define_63y3pe$(result, component);
  }
  function Komponent() {
    Komponent$Companion_getInstance();
    this.element = null;
    this.rendered = false;
  }
  Komponent.prototype.create = function () {
    var elem = this.element;
    if (elem != null) {
      Komponent$Companion_getInstance().remove_2rdptt$(elem);
    }
    elem = this.render_q0cphf$(get_create(document));
    this.rendered = true;
    Komponent$Companion_getInstance().define_63y3pe$(elem, this);
    this.element = elem;
    return elem;
  };
  Komponent.prototype.refresh = function () {
    if (this.rendered) {
      Komponent$Companion_getInstance().refresh_ejp6n4$(this.element);
    }
     else {
      this.update();
    }
  };
  Komponent.prototype.update = function () {
    Komponent$Companion_getInstance().refresh_ejp6n4$(this.element);
  };
  function Komponent$Companion() {
    Komponent$Companion_instance = this;
    this.elements_0 = HashMap_init();
    this.elementList_0 = ArrayList_init();
  }
  Komponent$Companion.prototype.define_63y3pe$ = function (element, component) {
    this.elements_0.put_xwzc9p$(element, component);
    this.elementList_0.add_11rb$(component);
  };
  Komponent$Companion.prototype.create_nkol39$ = function (parent, component, insertAsFirst) {
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
    this.elementList_0.add_11rb$(component);
  };
  Komponent$Companion.prototype.remove_2rdptt$ = function (element) {
    var component = this.elements_0.get_11rb$(element);
    this.elements_0.remove_11rb$(element);
    var $receiver = this.elementList_0;
    var tmp$;
    (Kotlin.isType(tmp$ = $receiver, Kotlin.kotlin.collections.MutableCollection) ? tmp$ : Kotlin.throwCCE()).remove_11rb$(component);
  };
  Komponent$Companion.prototype.remove = function (component) {
    var tmp$;
    tmp$ = this.elements_0.entries.iterator();
    while (tmp$.hasNext()) {
      var tmp$_0 = tmp$.next();
      var key = tmp$_0.key;
      var value = tmp$_0.value;
      if (Kotlin.equals(value, component)) {
        this.elements_0.remove_11rb$(key);
      }
    }
    this.elementList_0.remove_11rb$(component);
  };
  Komponent$Companion.prototype.refresh_1smjna$ = function (component) {
    this.refresh_ejp6n4$(component.element);
  };
  Komponent$Companion.prototype.refresh_ejp6n4$ = function (element) {
    var tmp$;
    if (element != null) {
      if ((tmp$ = this.elements_0.get_11rb$(element)) != null) {
        var newElement = tmp$.create();
        var replacedElement = DomDiffer_getInstance().replaceDiff_26ucz2$(newElement, element);
        tmp$.element = replacedElement;
        this.elements_0.remove_11rb$(replacedElement);
        this.elements_0.put_xwzc9p$(replacedElement, tmp$);
      }
    }
  };
  Komponent$Companion.$metadata$ = {
    kind: Kotlin.Kind.OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Komponent$Companion_instance = null;
  function Komponent$Companion_getInstance() {
    if (Komponent$Companion_instance === null) {
      new Komponent$Companion();
    }
    return Komponent$Companion_instance;
  }
  Komponent.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'Komponent',
    interfaces: []
  };
  var package$nl = _.nl || (_.nl = {});
  var package$astraeus = package$nl.astraeus || (package$nl.astraeus = {});
  var package$komp = package$astraeus.komp || (package$astraeus.komp = {});
  Object.defineProperty(package$komp, 'DomDiffer', {
    get: DomDiffer_getInstance
  });
  package$komp.include_uhm0kd$ = include;
  package$komp.include_yzvepg$ = include_0;
  package$komp.include_ly6v3d$ = include_1;
  Object.defineProperty(Komponent, 'Companion', {
    get: Komponent$Companion_getInstance
  });
  package$komp.Komponent = Komponent;
  Kotlin.defineModule('komp', _);
  return _;
}(typeof komp === 'undefined' ? {} : komp, kotlin, this['kotlinx-html-js']);

//# sourceMappingURL=komp.js.map
