import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {PlaceServicesService} from "../services/place-services.service";
import {PointOfInterestManagementService} from "../services/point-of-interest-management.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ItineraryBucketService} from "../services/itinerary-bucket.service";
import {FavoritesService} from "../services/favorites.service";
import {NotificationService} from "../services/notification.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-point-of-interest-search',
  templateUrl: './point-of-interest-search.component.html',
  styleUrls: ['./point-of-interest-search.component.css']
})
export class PointOfInterestSearchComponent implements OnInit, OnDestroy {

  isLoading = true;

  paramIdUser: number;

  paramCity: string | null;
  paramRadius: number;
  paramKind: string | null;
  paramPlace: any | null;
  pointOfInterests: any;
  private authStatusSub: Subscription;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private placeService: PlaceServicesService,
              private pointOfInterestManagement: PointOfInterestManagementService,
              private itineraryBucket: ItineraryBucketService,
              private favoriteService: FavoritesService,
              private notificationService: NotificationService
  ) {
    this.paramIdUser = Number(this.route.snapshot.paramMap.get("idUser"))
    this.paramCity = this.route.snapshot.paramMap.get('city')
    this.paramRadius = Number(this.route.snapshot.paramMap.get('radius'))
    this.paramKind = this.route.snapshot.paramMap.get('kind')
    this.paramPlace = this.route.snapshot.paramMap.get('place')

    if (this.paramKind === 'null') this.paramKind = ''
    if (this.paramPlace === 'null') this.paramPlace = ''

    var body = {
      idUser: this.paramIdUser,
      city: this.paramCity,
      radius: this.paramRadius,
      kind: this.paramKind,
      place: this.paramPlace
    }

    this.pointOfInterestManagement.getUrl(body)

  }

  ngOnInit(): void {
    this.checkIfUserIsAlradyLoggedIn();
    this.authStatusSub = this.authService.getAuthStatusListener().subscribe(isAuthenticated => {
      this.checkIfUserIsAlradyLoggedIn();
    })
    console.log("Before the request")
    this.placeService.getPlacebyNameAndCountryLetter(this.paramCity, 'PT').subscribe(
      ((res: any) => {
        console.log("Chegou ", res)
        console.log(res.latitude, res.longitude, this.paramIdUser, this.paramPlace)

        this.pointOfInterestManagement
          .getListOfPointsByRadiusUserLatUserLong(this.paramRadius, res.latitude, res.longitude, this.paramKind, this.paramPlace, this.paramIdUser)
          .subscribe((resPlace: any) => {
            this.pointOfInterests = resPlace;
            this.isLoading = false;
          })
      })
    )
  }


  getPointInterestInfo(idPoint: any) {
    console.log("ESTE E O ID DO USER", this.paramIdUser)
    console.log('Este é o id ', idPoint)
    this.router.navigate([`pointOfInterest/${idPoint}`]);
  }

  addFavorite(xid: string, name: string, image: string) {
    console.log("Add to Favorite ", xid, this.paramIdUser)
    this.favoriteService.createFavorite(this.paramIdUser, xid, name, image)
      .subscribe((f) => {
        console.log("Answer favorite ", f);
        this.notificationService.showNotification("Added to favorites ", "success")
      })
  }

  addToItenerary(point: any) {
    this.itineraryBucket.addPoint(point)
  }

  checkIfUserIsAlradyLoggedIn() {
    let userIsAuthenticated = this.authService.getIsAuth();
    if (userIsAuthenticated === true) {
      let user = this.authService.getUser();
      this.paramIdUser = user.id;
      console.log("USER ID: ", this.paramIdUser)

    } else {
      this.paramIdUser = 0;
      console.log("USER ID: ", this.paramIdUser);
    }
  }

  ngOnDestroy(): void {
    this.authStatusSub.unsubscribe();
  }
}
