import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from '../../environments/environment';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class PointOfInterestReviewService {
  getReviewsListener: Subject<any> = new Subject<any>();

  baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  getPointOfInterestReviewDetailsByUserIDandXID(user_id: number, xid: string) {
    return this.http.get(this.baseUrl + `/pointsOfInterestReview/reviews/${user_id}/${xid}`)
  }


  getAllUserPointsOfInterestReviews(user_id: number) {
    return this.http.get(this.baseUrl + `/pointsOfInterestReview/reviews/users/${user_id}`)
  }

  getAllPointOfInterestReviewsGivenAnXid(xid: string) {
    this.http.get(this.baseUrl + `/pointsOfInterestReview/reviews/pointsOfInterest/${xid}`).subscribe(
      (reviews:any) =>{
        this.getReviewsListener.next(reviews)
      }
    )
  }

  getAllReviewsListenerUpdated(){
    return this.getReviewsListener.asObservable();
  }

  getPointOfInterestRateGivenXid(xid: string) {
    return this.http.get(this.baseUrl + `/pointsOfInterestReview/reviews/rate/${xid}`)
  }

  addReview(user_id: number, xid: string, commentary: string, rate: number) {
    var body = {
      user_id: user_id,
      xid: xid,
      commentary: commentary,
      rate: rate
    }
    return this.http.post(this.baseUrl+'/pointsOfInterestReview/reviews', body)
  }


}
