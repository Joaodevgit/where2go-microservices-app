import {Component, OnDestroy, OnInit} from '@angular/core';
import {Route, Router} from "@angular/router";
import {FavoritesService} from "../services/favorites.service";
import {AuthService} from "../services/auth.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-my-favorites',
  templateUrl: './my-favorites.component.html',
  styleUrls: ['./my-favorites.component.css']
})
export class MyFavoritesComponent implements OnInit, OnDestroy{

  pointSubs: Subscription;
  currentUser:any;

  constructor(private router: Router,
              private authService: AuthService,
              private favoriteService: FavoritesService) {
    this.currentUser= this.authService.getUser();
  }
  pointDatas:any;
  kinds: any;

  ngOnInit(): void {
    this.favoriteService.getAllFavoritesByUser(this.currentUser.id)
    this.pointSubs = this.favoriteService.getFavoritePointsListenerUpdated().subscribe((favoritePoints)=>{
      console.log("Favorites ", favoritePoints)
      this.pointDatas=favoritePoints
    })
  }


  clickKind(kind: any) {
    console.log("This kind was selected", kind)
  }


  addToItenerary() {
    console.log("Adding to itinerary....")
  }


  getPointInterestInfo(idPoint:any) {
    console.log('Este é o id ', idPoint)
    this.router.navigate([`pointOfInterest/${idPoint}`]);
  }


  removeFavorite(xid:any) {
    console.log("REMOVE FROM FAVORITES")
    this.favoriteService.deleteFavorite(xid, this.currentUser.id, this.pointDatas)
  }

  ngOnDestroy(): void {
    this.pointSubs.unsubscribe();
  }
}
