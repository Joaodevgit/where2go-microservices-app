import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PointOfInterestManagementService} from "../services/point-of-interest-management.service";
import {FavoritesService} from "../services/favorites.service";
import {NotificationService} from "../services/notification.service";
import {ItineraryBucketService} from "../services/itinerary-bucket.service";
import {AuthService} from "../services/auth.service";
import {PointOfInterestReviewService} from "../services/point-of-interest-review-service";
import moment from 'moment';
import {Subscription} from "rxjs";


@Component({
  selector: 'app-point-of-interest',
  templateUrl: './point-of-interest.component.html',
  styleUrls: ['./point-of-interest.component.css']
})
export class PointOfInterestComponent implements OnInit, OnDestroy {
  pointData: any;
  kinds: any;

  showR: boolean = false;
  private idPoint: any;

  public currentUser: any;

  private commentary: string

  private rate: number

  private body: any

  pointReviews: any

  allUsers: any;


  getUserListener: Subscription;
  getReviewsListener: Subscription;

  ratingNumber: any;
  selectedValue: any = 0;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService,
              private pointOfInterestManagement: PointOfInterestManagementService,
              private favoriteService: FavoritesService,
              private notificationService: NotificationService,
              private itineraryBucket: ItineraryBucketService,
              private reviewService: PointOfInterestReviewService
  ) {

    this.currentUser = this.authService.getUser();
    console.log("Comeocu ", this.currentUser)
    this.getUserListener = this.authService.getAuthStatusListener().subscribe((isAuthentic: any) => {
      this.currentUser = this.authService.getUser();
      console.log("Depiis ", this.currentUser)
    })

    this.idPoint = this.route.snapshot.paramMap.get('id')
    this.pointOfInterestManagement.getPointOfInterestByXid(this.idPoint).subscribe((pointOfInterest => {
      this.pointData = pointOfInterest;
      console.log("Aqui ", pointOfInterest)
      if (this.pointData.kinds.length !== 0) {
        this.prepareKinds((kinds: any) => {
          this.kinds = kinds;
        })
      }
    }))

  }

  ngOnInit(): void {
    console.log("this userService: ", this.currentUser)
    this.authService.getAllUser().subscribe((users: any) => {
      /*this.reviewService.getAllPointOfInterestReviewsGivenAnXid(this.idPoint).subscribe((reviews) => {
        console.log("REVIEWS", reviews)
        this.pointReviews = reviews
        this.addInfoToTheReview(reviews, users)

      })*/

      this.reviewService.getAllPointOfInterestReviewsGivenAnXid(this.idPoint)
      this.getUserListener = this.reviewService.getAllReviewsListenerUpdated().subscribe((reviews) => {
        console.log("REVIEWS", reviews)
        this.pointReviews = reviews
        this.addInfoToTheReview(reviews, users)

      })
    });
  }

  ngOnDestroy() {
    this.getUserListener.unsubscribe();
  }

  prepareKinds(kinds: any) {
    console.log("Dentro do kinds")
    const kindBody: any = [];
    var firstRemoval = this.pointData.kinds.replace('[', '').replace(']', '')
    var arrayKind = firstRemoval.split(',')
    console.log(arrayKind)
    arrayKind.forEach((kind: any) => {
      var kindaux = kind.replaceAll('_', ' ').trim();
      kindaux = kindaux.charAt(0).toUpperCase() + kindaux.slice(1);
      var body = {name: kindaux, id: kind}
      kindBody.push(body)
    })
    return kinds(kindBody)
  }


  clickKind(kind: any) {
    console.log("This kind was selected", kind)
    //Por route para essas kind
    var previousURL = this.pointOfInterestManagement.sendURL();
    if (previousURL.place === '') {
      previousURL.place = null
    }
    this.router.navigate([`search/${previousURL.idUser}/${previousURL.city}/${previousURL.radius}/${kind.id}/${previousURL.place}`])
  }

  addFavorite(xid: string, name: string, image: string) {
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

  addReview(xid: string) {

    //console.log("XID::::::::::::::::::::", xid)
    const inputCommentary = document.getElementById("comment") as HTMLInputElement;
    const inputRate = document.getElementById("rate") as HTMLInputElement;

    this.commentary = inputCommentary.value;
    // this.rate = parseInt(inputRate.value);

    //console.log("COMENTARIO FEITO:::::", this.commentary)
    // console.log("rate:::::", this.rate)
    //console.log("Add Review")
    //console.log("USER ID:::::::::::::::", this.currentUser.id)
    //console.log("PONTO::::::::::::::", xid)

    console.log("Comentário: ", this.commentary)
    console.log("Rating: ", this.selectedValue)

    if (this.commentary=== null || this.selectedValue===0){
      console.log("Esta null")
      return;
    }
    this.reviewService.addReview(this.currentUser.id, xid, this.commentary, this.selectedValue)
      .subscribe((review) => {
        console.log("REVIEW FEITA:::::::::::: ", review);
        this.reviewService.getAllPointOfInterestReviewsGivenAnXid(this.idPoint)
        this.showR=false;
      })
  }

  addInfoToTheReview(reviews: any, users: any) {
    console.log("Users ", users)
    console.log("Reviews ", reviews)

    const reviewsWithUserName = addUserNameToReviews(reviews, users);
    console.log("TESTE ", reviewsWithUserName)
    this.pointReviews = reviewsWithUserName;

    function addUserNameToReviews(reviews: any[], users: any[]): any[] {
      return reviews.map((review: any) => {
        const user = users.find((u) => u.id === review.user_id);
        var name = user.firstname + ' ' + user.lastname
        return {...review, userName: name};
      });
    }
  }

  showReview() {
    this.showR = !this.showR;
  }

  moment(reviewDate: any) {
    return moment(reviewDate).format('DD-MM-YYYY HH:mm')
  }

  sendRating($event: any) {
    console.log("Aqui no evento")
    console.log("Aqui ", $event)
  }

  onChange(value: any) {
    console.log("valores ", value)
  }
}
