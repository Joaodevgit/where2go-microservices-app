import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from '../../environments/environment';

@Injectable({

  providedIn: 'root'
})
export class PlaceServicesService {

  baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  /**
   * Get a certain place given its search result name
   *
   * @param name "Porto"
   * @param letter "PT"
   *
   */
  getPlacebyNameAndCountryLetter(name: string | null, letter: string) {
    return this.http.get(this.baseUrl + `/placesSearch/place/${name},${letter}`)
  }


  /**
   * Get a certain placed based on latitude and longitude
   *
   * @param lat 41.15794
   * @param long -8.629105
   *
   */
  getPlaceByUserLatitudeAndLongitude(lat: number, long: number) {
    this.http.get(this.baseUrl + `/placesSearch/userPlace/${lat}/${long}`)
  }

}
