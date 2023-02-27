import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Subject} from "rxjs";
import {environment} from '../../environments/environment';

@Injectable({

  providedIn: 'root'
})
export class FavoritesService {
  baseUrl = environment.apiUrl;
  private favoritePointsSub = new Subject<any>();

  constructor(private http: HttpClient) {
  }


  getAllFavorites() {
    return this.http.get(this.baseUrl + `/favorite`)
  }

  /**
   *Get all user favorites
   *
   * @param user_id 1
   */
  getAllFavoritesByUser(user_id: number) {
    this.http.get(this.baseUrl + `/favorite/user/${user_id}`)
      .subscribe((favoritePoints) => {
        this.favoritePointsSub.next(favoritePoints)
      })
  }

  /**
   *
   *
   * @param pointOfInterest_id 1
   */
  getAllFavoritesByPointOfInterest(pointOfInterest_id: number) {
    this.http.get(this.baseUrl + `/favorite/point-of-interest/${pointOfInterest_id}`)
  }


  /**
   *
   *
   * @param pointOfInterest_id 1
   */
  getNumberOfFavoritesByPointOfInterest(pointOfInterest_id: number) {
    this.http.get(`http://localhost:8000/favorite/point-of-interest-count/${pointOfInterest_id}`)
  }

  createFavorite(user_id: number, xid: string, favorite_name: string, url: string) {
    var body = {
      user_id: user_id,
      xid: xid,
      favoriteName: favorite_name,
      url: url
    }
    return this.http.post(this.baseUrl + '/favorite', body)
  }


  /**
   * Confirmar pedido no swagger
   * Delete favorite
   *
   * @param favorite_id 2
   * @param user_id 1
   *
   */
  deleteFavorite(favorite_id: number, user_id: number, favoritePoints: any) {
    this.http.delete(this.baseUrl + `/favorite/${user_id}/${favorite_id}`)
      .subscribe(deleteRes => {
        console.log("Deleted ", deleteRes)
        favoritePoints = favoritePoints.filter((i: any) => i.xid !== favorite_id);
        this.favoritePointsSub.next(favoritePoints)
      })
  }

  getFavoritePointsListenerUpdated() {
    return this.favoritePointsSub.asObservable();
  }

}
