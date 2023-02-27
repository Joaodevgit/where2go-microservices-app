import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {PlaceServicesService} from "../services/place-services.service";
import {Subscription} from "rxjs";
import {PointOfInterestManagementService} from "../services/point-of-interest-management.service";
import {Route, Router} from "@angular/router";
import {MatPaginator} from "@angular/material/paginator";
import {NgForm} from "@angular/forms";
import {FavoritesService} from "../services/favorites.service";
import {NotificationService} from "../services/notification.service";
import {ItineraryBucketService} from "../services/itinerary-bucket.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit, OnDestroy {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  isLoading: boolean = false;
  optionFormAppear: boolean = false;
  private isAuth: any;

  idUser: number;

  authStatusSub: Subscription;
  private currentUser: any;

  imagesPorto: any;
  imagesLisboa: any;
  imagesFaro: any;

  currentIndexPorto = 0;
  currentIndexLisboa = 0;
  currentIndexFaro = 0;
  cityPoints = [
    {city: 'Porto', latitude: 41.1495, longitude: -8.6108},
    {city: 'Lisboa', latitude: 38.7452, longitude: -9.1604},
    {city: 'Faro', latitude: 37.0161, longitude: -7.935}
  ]
  portoPoints: any;
  lisboaPoints: any;
  algarvePoints: any

  showMatSpinner:boolean=true;

  getKindHeaderUpdated: Subscription;


  constructor(private router: Router, private authService: AuthService, private placeService: PlaceServicesService,
              private favoriteService: FavoritesService, private notificationService: NotificationService,
              private itineraryBucket: ItineraryBucketService,
              private pointOfInterestManagement: PointOfInterestManagementService) {

  }

  ngOnInit(): void {
    this.showMatSpinner=true

    this.checkIfUserIsAlradyLoggedIn();
    this.authStatusSub = this.authService.getAuthStatusListener().subscribe(isAuthenticated => {
      this.checkIfUserIsAlradyLoggedIn();
    })

    this.getPointsMultipleCities('',(done: any) => {
      this.showMatSpinner=false;
      this.showNextImages('all')
    })

    this.getKindHeaderUpdated=this.pointOfInterestManagement.sendKind()
      .subscribe((kind:string) =>{
        console.log("kind ", kind)
        this.getPointsMultipleCities(kind,(done: any) => {
          console.log(this.portoPoints)
          console.log(this.lisboaPoints)
          console.log(this.algarvePoints)
          this.showNextImages('all')
        })
      })
  }

  ngOnDestroy() {
    this.authStatusSub.unsubscribe();
    this.getKindHeaderUpdated.unsubscribe();
  }

  onSearch(pointSearch: NgForm) {
    if (pointSearch.value.radius == "" || pointSearch.value.city == "") {
      return;
    }

    this.isLoading = true;
    var city = pointSearch.value.city
    var radius = Number(pointSearch.value.radius) * 1000
    var place = pointSearch.value.place || 'null'
    this.pointOfInterestManagement.cleanUrl();
    this.router.navigate([`search/${this.idUser}/${city}/${radius}/null/${place}`])
  }

  checkIfUserIsAlradyLoggedIn() {
    let userIsAuthenticated = this.authService.getIsAuth();
    if (userIsAuthenticated === true) {
      let user = this.authService.getUser();
      this.idUser = user.id;
      console.log("USER ID: ", this.idUser)

    } else {
      this.idUser = 0;
      console.log("USER ID: ", this.idUser);
    }
  }

  showOptionForm() {
    this.optionFormAppear = !this.optionFormAppear;
  }

  showNextImages(target: string) {
    if (target === 'all') {
      aux(this.imagesPorto, this.portoPoints, this.currentIndexPorto, (res: any) => {
        this.imagesPorto = res.images;
        this.currentIndexPorto = res.index;
      })
      aux(this.imagesLisboa, this.lisboaPoints, this.currentIndexLisboa, (res: any) => {
        this.imagesLisboa = res.images;
        this.currentIndexLisboa = res.index;
      })
      aux(this.imagesFaro, this.algarvePoints, this.currentIndexFaro, (res: any) => {
        this.imagesFaro = res.images;
        this.currentIndexFaro = res.index;
      })


    } else if (target === 'Porto') {
      aux(this.imagesPorto, this.portoPoints, this.currentIndexPorto, (res: any) => {
        this.imagesPorto = res.images;
        this.currentIndexPorto = res.index;
      })
    } else if (target === 'Lisboa') {
      aux(this.imagesLisboa, this.lisboaPoints, this.currentIndexLisboa, (res: any) => {
        this.imagesLisboa = res.images;
        this.currentIndexLisboa = res.index;
      })
    } else {
      aux(this.imagesFaro, this.algarvePoints, this.currentIndexFaro, (res: any) => {
        this.imagesFaro = res.images;
        this.currentIndexFaro = res.index;
      })
    }

    function aux(images: any, points: any, index: any, resAux: any) {
      images = points.slice(index, index + 3);
      index += 3;
      if (index >= points.length) {
        index = 0;
      }
      var body = {images: images, index: index}
      return resAux(body)
    }
  }

  showPreviewImages(target: string) {
    if (target === 'Porto') {
      aux(this.imagesPorto, this.portoPoints, this.currentIndexPorto, (res: any) => {
        this.imagesPorto = res.images;
        this.currentIndexPorto = res.index;
      })
    } else if (target === 'Lisboa') {
      aux(this.imagesLisboa, this.lisboaPoints, this.currentIndexLisboa, (res: any) => {
        this.imagesLisboa = res.images;
        this.currentIndexLisboa = res.index;
      })
    } else {
      aux(this.imagesFaro, this.algarvePoints, this.currentIndexFaro, (res: any) => {
        this.imagesFaro = res.images;
        this.currentIndexFaro = res.index;
      })
    }

    function aux(images: any, points: any, index: any, resAux: any) {
      images = points.slice(index, index + 3);
      index -= 3;
      if (index < 0) {
        index = images.length - 3;
      }
      images = images.slice(index, index + 3);
      var body = {images: images, index: index}
      return resAux(body)
    }
  }


  getPointInterestInfo(idPoint: any) {
    console.log('Este é o id ', idPoint)
    this.router.navigate([`pointOfInterest/${idPoint}`]);
  }


  getPointsMultipleCities(kind:string, done: any) {
    this.showMatSpinner=true;
    console.log("Aqui no multiple citire")
    this.portoPoints = [];
    this.lisboaPoints = [];
    this.algarvePoints = [];
    this.cityPoints.forEach(city => {
      this.pointOfInterestManagement.getListOfPointsByRadiusUserLatUserLong(100000, city.latitude, city.longitude, kind, '', this.idUser)
        .subscribe((pointsCity) => {
          if (city.city === 'Porto') {
            this.portoPoints = pointsCity;
          } else if (city.city === 'Faro') {
            this.algarvePoints = pointsCity
          } else {
            this.lisboaPoints = pointsCity;
          }
          if (this.portoPoints.lengtn !== 0 && this.lisboaPoints.length !== 0 && this.algarvePoints.length !== 0) {
            return done(true)
          }
        })
    })


    function processItemWithDelay(delay: any) {
      setTimeout(() => {
      }, delay);
    }
  }

  addFavorite(xid: string, name:string, image: string) {
    console.log("Adding to favorites...")
    this.favoriteService.createFavorite(this.currentUser.id, xid, name, image)
      .subscribe((f) => {
        console.log("Answer favorite ", f);
        this.notificationService.showNotification("Added to favorites ", "success")
      })
  }

  addToItenerary(point: any) {
    console.log("Adding to itinerary....")
    this.itineraryBucket.addPoint(point)
  }
}
