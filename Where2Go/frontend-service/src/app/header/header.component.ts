import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {AuthComponent} from "../auth/auth.component";
import {Subscriber, Subscription} from "rxjs";
import {AuthService} from "../services/auth.service";
import {ItineraryBucketService} from "../services/itinerary-bucket.service";
import {MatTabChangeEvent} from "@angular/material/tabs";
import {PointOfInterestManagementService} from "../services/point-of-interest-management.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {


  numberLength: string | '0';

  @Input() backgroundColor: any;

  @Output() toggleSideBarForMe: EventEmitter<any> = new EventEmitter<any>();
  userIsAuthenticated = false;

  authStatusSub: Subscription;
  lengthSub: Subscription;

  constructor(public dialog: MatDialog,
              private authService: AuthService,
              private pointOfInterestManagement: PointOfInterestManagementService,
              public itineraryBucket: ItineraryBucketService,
              public router:Router
  ) {
    this.backgroundColor = "#6e3e1c"

    this.lengthSub = this.itineraryBucket.returnLengthListesnerUpdated().subscribe((lengthIt => {
      this.numberLength = String(lengthIt);
    }))
  }


  toggleSideBar() {
    console.log("Clicou no butao")
    this.toggleSideBarForMe.emit();
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 300);
  }

  openDialog() {
    const email = null;
    const dialogRef = this.dialog.open(AuthComponent, {
      data: {email: email}
    });
  }


  closeDialog() {
    this.dialog.closeAll();
  }

  ngOnInit() {
    this.userIsAuthenticated = this.authService.getIsAuth();
    this.authStatusSub = this.authService.getAuthStatusListener().subscribe(isAuthenticated => {
      this.userIsAuthenticated = isAuthenticated;
    })

    this.authService.aClickedEvent.subscribe(() => {
      this.dialog.closeAll();
    })

  }

  ngOnDestroy(): void {
    this.authStatusSub.unsubscribe();
  }


  tabChanged($event: MatTabChangeEvent) {
    var kind = ''
    const selectedTab = $event;
    var name = (selectedTab.tab.textLabel)
    if (name === 'Restaurantes') {
      kind = 'foods';
    } else if (name == 'Hóteis') {
      kind = 'accomodations'
    } else if (name === 'Museus') {
      kind = 'museums'
    } else if (name === 'Geral') {
      kind = ''
    } else {
      kind = 'monuments_and_memorials'
    }
    this.pointOfInterestManagement.getKind(kind)
    this.router.navigate([''])

  }
}

