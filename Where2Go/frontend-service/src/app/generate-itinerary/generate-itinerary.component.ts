import {Component, OnInit} from '@angular/core';
import {ItineraryBucketService} from "../services/itinerary-bucket.service";
import {Router} from "@angular/router";
import {NotificationService} from "../services/notification.service";
import {Subscription} from "rxjs";
import {ItineraryService} from "../services/itinerary.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-generate-itinerary',
  templateUrl: './generate-itinerary.component.html',
  styleUrls: ['./generate-itinerary.component.css']
})
export class GenerateItineraryComponent implements OnInit {
  points: any;
  route:any;
  public travelMode: any = 'WALKING'
  pointSubs: Subscription;
  genInitSub: Subscription;

  genItinerary: any;

  isGenerated: boolean = false;

  idUserCurrent:number;

  constructor(private itineraryBucket: ItineraryBucketService,
              private router: Router,
              private notification: NotificationService,
              private itineraryService: ItineraryService,
              private authService: AuthService
  ) {
    let currentUser = this.authService.getUser();
    this.idUserCurrent = currentUser.id;
  }

  ngOnInit(): void {
    if (this.itineraryBucket.getItems().length == 0) {
      this.notification.showNotification('Add more points', 'warning')
      this.router.navigate([''])
    }
    this.points = this.itineraryBucket.getItems();
    console.log('THIS POINTS', this.points)

    this.pointSubs = this.itineraryBucket.getAllPointsUpdated().subscribe((items => {
      this.points = items;
      console.log("this points ", this.points)
    }))
  }

  removeItem(point: any) {
    this.itineraryBucket.removeFromHere(point);
  }

  generateItinerary() {
    console.log("Ready to generate ", this.points)
    const newItineraryInfo = this.points.map((itinerary: any) => {
      return {
        xid: itinerary.xid,
        user_id: this.idUserCurrent,
        name: itinerary.name,
        latitude: itinerary.latitude,
        longitude: itinerary.longitude
      }
    });

    this.itineraryService.generateItinerary(newItineraryInfo).subscribe(((itineraryRes: any) => {
      console.log("Generated ", itineraryRes)
      this.genItinerary = itineraryRes;
      if (itineraryRes.length !== 0) {
        this.isGenerated = true;
      }
      this.createPath(itineraryRes)
      //this.router.navigate(['itineraryInfo'])
    }))

  }

  //
  saveItinerary() {

    this.itineraryService.saveItinerary(this.genItinerary)
      .subscribe((resSave) => {
        console.log("Save ", resSave)
        if(resSave == null){
          this.notification.showNotification("Itinerary saved", "success")
          this.itineraryBucket.clear();
          this.router.navigate(['myItinerary'])

        }
      })

  }


  createPath(itinerary: any) {
    var origin = {};
    var originInfoWindow = {}

    var destination = {};
    var destinationWindow = {}

    var waypoints = [];
    var waypointInfoWindow = []
    for (let i = 0; i < itinerary.length; i++) {
      if (i === 0) {
        origin = {lat: itinerary[i].latitude, lng: itinerary[i].longitude}
        originInfoWindow = {
          visible: false,
          //infoWindow: itinerary[i].pointOfInterestName
        }
      } else if (i === itinerary.length - 1) {
        destination = {lat: itinerary[i].latitude, lng: itinerary[i].longitude}
        destinationWindow = {
          //infoWindow: itinerary[i].pointOfInterestName
        }
      } else {
        waypoints.push({
          location: {lat: itinerary[i].latitude, lng: itinerary[i].longitude},

        })
        waypointInfoWindow.push({
          //infoWindow:itinerary[i].infoWindow
        })
      }
    }

    this.route = {
      origin: origin,
      destination: destination,
      waypoints: waypoints,
      renderOptions: {
        suppressMarkers: true,
      },
    }
  }



}
