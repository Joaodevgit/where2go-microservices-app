import {Component, OnDestroy, OnInit} from '@angular/core';
import {ItineraryService} from "../services/itinerary.service";
import {AuthService} from "../services/auth.service";
import {PointOfInterestManagementService} from "../services/point-of-interest-management.service";
import {delay, Subscription} from "rxjs";
import {DialogService} from "../mat-confirm-dialog/dialog.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-my-itinerary',
  templateUrl: './my-itinerary.component.html',
  styleUrls: ['./my-itinerary.component.css']
})
export class MyItineraryComponent implements OnInit, OnDestroy {

  itineraries: any;
  idCurrentUser: any;
  routes: any;
  dataLoaded = false;
  public travelMode: any = 'WALKING'

  public itinerarySub: Subscription;

  constructor(private itineraryService: ItineraryService,
              private authService: AuthService,
              private pointOfInterestService: PointOfInterestManagementService,
              private confirmDialog: DialogService,
              private router:Router
  ) {
    var currentUser = this.authService.getUser();
    this.idCurrentUser = currentUser.id;
    console.log(this.idCurrentUser)
  }

  ngOnInit(): void {
    this.routes = [];
    this.itineraryService.getAllItineraryByUser(this.idCurrentUser)
    this.itinerarySub = this.itineraryService.getItinerariesByUserListenerUpdated()
      .subscribe((resIt: any) => {
        this.itineraries = resIt.itineraries;
        this.routes= resIt.routes;
        this.dataLoaded = true

        console.log("Routes ", this.routes)
        console.log("Itineraries ", this.itineraries)

      })
  }






  deleteItinerary(itineraryID: any) {
    this.confirmDialog.openConfirmDialog("Are you sure you want to delete this?").afterClosed().subscribe(
      (res: any) => {
        if (res === true) {
          console.log(itineraryID)
          this.itineraryService.deleteItinerary(itineraryID, this.idCurrentUser)
        } else {
          return;
        }
      }
    )
  }

  ngOnDestroy(): void {
    this.itinerarySub.unsubscribe();
  }

  getPointInterestInfo(xid: any) {
    console.log("Aqui está o xid ")
    this.router.navigate([`pointOfInterest/${xid}`]);
  }
}
