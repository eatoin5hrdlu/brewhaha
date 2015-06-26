diam = 70;
rad = diam/2;
height = 25;

module cap() {
difference() {
    cylinder(r1=rad+4,r2=rad+12,h=height);
// rad-0.5 outer
// rad=1.0 inner
    translate([0,0,5])
       cylinder(r=rad+0.5,h=height,$fn=128); //OUTER
}
cylinder(r=rad-1,h=25,$fn=128); // INNER
}


module scallop() {
  translate([0,-46,-4])
    rotate([4,0,0]) 
      cylinder(r=7,h=40,$fn=18);
}

difference() {
cap();
translate([4,4,-2]) cylinder(r=2,h=height+10,$fn=10);
translate([-4,-4,-2]) cylinder(r=1.5,h=height+10,$fn=8);
for ( az = [ 0 : 17.14 :345]) {
	rotate([0,0,az]) scallop();
}
//translate([0,0,height/4])cube([90,90,height],center=true);
}



