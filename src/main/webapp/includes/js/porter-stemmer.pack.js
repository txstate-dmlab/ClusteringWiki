var stemmer=(function(){var h={ational:"ate",tional:"tion",enci:"ence",anci:"ance",izer:"ize",bli:"ble",alli:"al",entli:"ent",eli:"e",ousli:"ous",ization:"ize",ation:"ate",ator:"ate",alism:"al",iveness:"ive",fulness:"ful",ousness:"ous",aliti:"al",iviti:"ive",biliti:"ble",logi:"log"},e={icate:"ic",ative:"",alize:"al",iciti:"ic",ical:"ic",ful:"",ness:""},i="[^aeiou]",k="[aeiouy]",a=i+"[^aeiouy]*",g=k+"[aeiou]*",f="^("+a+")?"+g+a,b="^("+a+")?"+g+a+"("+g+")?$",d="^("+a+")?"+g+a+g+a,j="^("+a+")?"+k;return function(q){var l,s,r,t,o,n,m,c=q;if(q.length<3){return q}r=q.substr(0,1);if(r=="y"){q=r.toUpperCase()+q.substr(1)}t=/^(.+?)(ss|i)es$/;o=/^(.+?)([^s])s$/;if(t.test(q)){q=q.replace(t,"$1$2")}else{if(o.test(q)){q=q.replace(o,"$1$2")}}t=/^(.+?)eed$/;o=/^(.+?)(ed|ing)$/;if(t.test(q)){var p=t.exec(q);t=new RegExp(f);if(t.test(p[1])){t=/.$/;q=q.replace(t,"")}}else{if(o.test(q)){var p=o.exec(q);l=p[1];o=new RegExp(j);if(o.test(l)){q=l;o=/(at|bl|iz)$/;n=new RegExp("([^aeiouylsz])\\1$");m=new RegExp("^"+a+k+"[^aeiouwxy]$");if(o.test(q)){q=q+"e"}else{if(n.test(q)){t=/.$/;q=q.replace(t,"")}else{if(m.test(q)){q=q+"e"}}}}}}t=/^(.+?)y$/;if(t.test(q)){var p=t.exec(q);l=p[1];t=new RegExp(j);if(t.test(l)){q=l+"i"}}t=/^(.+?)(ational|tional|enci|anci|izer|bli|alli|entli|eli|ousli|ization|ation|ator|alism|iveness|fulness|ousness|aliti|iviti|biliti|logi)$/;if(t.test(q)){var p=t.exec(q);l=p[1];s=p[2];t=new RegExp(f);if(t.test(l)){q=l+h[s]}}t=/^(.+?)(icate|ative|alize|iciti|ical|ful|ness)$/;if(t.test(q)){var p=t.exec(q);l=p[1];s=p[2];t=new RegExp(f);if(t.test(l)){q=l+e[s]}}t=/^(.+?)(al|ance|ence|er|ic|able|ible|ant|ement|ment|ent|ou|ism|ate|iti|ous|ive|ize)$/;o=/^(.+?)(s|t)(ion)$/;if(t.test(q)){var p=t.exec(q);l=p[1];t=new RegExp(d);if(t.test(l)){q=l}}else{if(o.test(q)){var p=o.exec(q);l=p[1]+p[2];o=new RegExp(d);if(o.test(l)){q=l}}}t=/^(.+?)e$/;if(t.test(q)){var p=t.exec(q);l=p[1];t=new RegExp(d);o=new RegExp(b);n=new RegExp("^"+a+k+"[^aeiouwxy]$");if(t.test(l)||(o.test(l)&&!(n.test(l)))){q=l}}t=/ll$/;o=new RegExp(d);if(t.test(q)&&o.test(q)){t=/.$/;q=q.replace(t,"")}if(r=="y"){q=r.toLowerCase()+q.substr(1)}return q}})();