
module cap() {
difference() {
    cylinder(r1=15,r2=18,h=10);
    translate([0,0,3]) cylinder(r=13,h=10,$fn=128);
}
cylinder(r=11,h=10,$fn=128);
}


module scallop() {
  translate([0,-19,-4])
    rotate([5,0,0]) 
      cylinder(r=4,h=20,$fn=18);
}

difference() {
cap();
translate([4,4,-2]) cylinder(r=1.5,h=14,$fn=10);
translate([-4,-4,-2]) cylinder(r=1,h=14,$fn=8);
for ( az = [ 0 : 20 :340]) {
	rotate([0,0,az]) scallop();
}
}



