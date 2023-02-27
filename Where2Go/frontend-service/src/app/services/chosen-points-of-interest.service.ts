import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChosenPointsOfInterestService {

  baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }


  /**
   * Delete a certain chosen point of interest of a certain user given user id
   *
   * @param user_id 1
   * @param xid 1
   */
  deletePointOfInterest(user_id: number, xid: string) {
    this.http.delete(this.baseUrl + `/chosenPoints/${user_id}/${xid}`)
  }

  /**
   * Save user chosen point of interest
   *
   * @param xid "W13531471",
   * @param user_id 1
   * @param name "Jardim da Rotunda da Boavista"
   */
  savePointOfInterest(xid: string, user_id: number, name: string) {
    var body = {
      xid: xid,
      user_id: user_id,
      name: name
    }
    this.http.post(this.baseUrl + '/chosenPoints/save', body)
  }

  /**
   * Get all chosen points of interest of a certain user given user id
   *
   * @param user_id 1
   */
  getAllPointOfInterestByUser(user_id: number) {
    this.http.get(this.baseUrl + `/chosenPoints/${user_id}`)
  }
}
