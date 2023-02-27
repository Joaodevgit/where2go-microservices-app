import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SearchHistoryService {
  baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  /**
   * Get the last 5 searches made by the user
   *
   * @param user_id 1
   *
   */
  getLast5SearchesByUser(user_id: number) {

    return this.http.get(this.baseUrl + `/pointsOfInterestSearchHistory/searches/${user_id}`)
  }
}
