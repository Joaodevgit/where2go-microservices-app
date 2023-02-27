import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Subject} from "rxjs";
import {environment} from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class PointOfInterestManagementService {

  baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  kindHeaderListener: Subject<any> = new Subject<any>();

  urlBody: any
  kindHeader: string = ''

  /**
   *
   * @param xid "W286005374"
   *
   */
  getPointOfInterestByXid(xid: string) {
    return this.http.get(this.baseUrl + `/pointOfInterestManagement/${xid}`)
  }

  /**
   *
   *
   *
   */
  getAllPointsOfInterest() {
    this.http.get(this.baseUrl + `/pointOfInterestManagement/`)
  }


  getListOfPointsByRadiusUserLatUserLong(radius: number, lat: number, long: number, kind: string | null, nameSearched: string, userId: number) {
    return this.http.get(this.baseUrl + `/pointOfInterestManagement/searchPointsInterest/radius=${radius}_lon=${long}_lat=${lat}_kinds=${kind}_search=${nameSearched}_${userId}`)
  }

  getPointOfInterestByXidList(xidList: string[]) {
    this.http.post(this.baseUrl + `/pointOfInterestManagement/listXid`, {xidList})
  }

  getUrl(url: any) {
    this.urlBody = url
  }

  sendURL() {
    return this.urlBody
  }

  cleanUrl() {
    this.urlBody = null;
  }

  getFirstKind() {
    this.kindHeaderListener.next('')
  }

  getKind(kind: string) {
    this.kindHeaderListener.next(kind)
  }

  sendKind() {
    return this.kindHeaderListener.asObservable();
  }
}
