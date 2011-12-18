(function($) {

	$.widget("ui.altselect", {
		options: {
			//Default
			width: 280,
			height: 100,
			selecthide: true
		},
		
		_create: function() {
			var self = this,
			o  = self.options, //options
			el = self.element; //dom element (selected element)
					
			o.selecthide ? el.hide() : el.show();
			
			self.container = $("<div></div>")
			.addClass("ui-altselect ui-helper-clearfix ui-widget")
			.css({
				width: o.width,
				height: o.height
			})
			.insertAfter(el);
			
			self.fltAutoComplete = $("<input type='text'/>");
			var header = $("<div></div>")
			.addClass("ui-widget-header ui-helper-clearfix")
			.append(self.fltAutoComplete)
			.appendTo(self.container);
			
			self.selectionList = $("<ul></ul>")
			.addClass("list");
			var list = $("<div></div>")
			.addClass("ui-widget-content")
			.append(self.selectionList)
			.appendTo(self.container);
			
			//fix height list
			list.height(o.height-28); //temp fix
			//list.height(Math.max(o.height-header.height(),1)); //not working on tabs
			
			self.selectoptions = [];
			
			el.find("option").each(function () {
				self.selectoptions.push( { label: this.text, value: this.value } );
				
				if (this.selected) {
					self._addToList(this, self.selectionList);
				}
			});
			
			self.fltAutoComplete.autocomplete({
				source: self.selectoptions,
				
				focus: function( event, ui ) {
					return false;
				},
				
				select: function( event, ui ) {
					self.fltAutoComplete.val("");
					self._addToList( { text: ui.item.label, value: ui.item.value }, self.selectionList);
					return false;
				}
			});
			
		},
		
		_addToList: function( item, list ) {
			var self = this,
			o  = self.options,
			el = self.element;
			
			$("<li class='ui-state-default ui-element' style='display: block;' id='select-"+item.value+"'>"+item.text+"</li>")
			.append(
				$("<a style='position: absolute; right: 0px;' href='#'><span class='ui-icon ui-icon-trash'></span></a>")
				.click(function() {
					el.find("option[value='"+item.value+"']").removeAttr("selected");
					list.find("#select-"+item.value).remove();
				})
			)
			.appendTo(list);
			
			el.find("option[value='"+item.value+"']").attr("selected","selected");			
		},
		
		_setOption: function(option, value) {
			$.Widget.prototype._setOption.apply( this, arguments ); //default
			
			var self = this,
			el = self.element;
			
			switch (option) { 
			case "width":
				self.container.width(value);
				break;
			case "height":
				self.container.height(value);
				self.selectionList.parent("div").height(value - 28); //temp fix
				break;
			case "selecthide":
				value ? el.hide() :	el.show();
				break;
			}
		},
		
		destroy: function() { 
			var self = this,
			o  = self.options,
			el = self.element;
			
			el.show();
			self.container.remove();
			
			$.Widget.prototype.destroy.apply(this, arguments); // default
		}
	});

})(jQuery);
