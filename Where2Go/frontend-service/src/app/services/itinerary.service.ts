import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Subject} from "rxjs";
import {PointOfInterestManagementService} from "./point-of-interest-management.service";
import {environment} from '../../environments/environment';

interface bodyItinerary {
  xid: string;
  userId: number;
  name: string;
  latitude: number;
  longitude: number;
}

interface bodySaveItinerary {

  itinerary_id: number;
  user_id: number;
  xid: string;
  pointOfInterestName: string;
  distance: number;
  latitude: number;
  longitude: number;
  "routeOrder": number;
  "itineraryDate": string;
}

@Injectable({
  providedIn: 'root'
})
export class ItineraryService {

  baseUrl = environment.apiUrl;

  private generatedItineraryListener = new Subject<any>();

  private itinerariesByUserListener = new Subject<any>();
  private itineraries: any;
  private routes: any;


  constructor(private http: HttpClient, private pointOfInterestService: PointOfInterestManagementService) {
  }

  /**
   * Get points of interest of a certain itinerary
   *
   * @param user_id 1
   * @param itinerary_id 1
   */
  getItineraryByUserIDIteneraryID(user_id: number, itinerary_id: number) {
    this.http.get(this.baseUrl + `/pointOfInterestItinerary/${user_id}/${itinerary_id}`)
  }

  /**
   *Get all user itineraries
   *
   * @param user_id 1
   */
  getAllItineraryByUser(user_id: number) {
    var routes: any = [];
    var auxIt: any = [];
    let delay = 0;
    this.http.get(this.baseUrl + `/pointOfInterestItinerary/${user_id}`).subscribe((
      itineraries: any
    ) => {
      itineraries.forEach((itinerary: any, indexI: number) => {
        createPath(itinerary, (route: any) => {
          routes.push(route)
          itinerary.forEach((point: any, indexP: number) => {
            processItemWithDelay(delay)
            this.pointOfInterestService.getPointOfInterestByXid(point.xid).subscribe((res: any) => {
              itineraries[indexI][indexP].image = res.image
              point.delay += 1000;
            })
          })
        })
      })


      itineraries.forEach((itinerary: any, index: number) => {
        var itinerary_id = itinerary[0].itinerary_id;
        var body = {
          itinerary_id: itinerary_id,
          itinerary: itinerary
        }
        auxIt.push(body)
      })

      this.itineraries = auxIt;
      this.routes = routes;
      var body = {itineraries: this.itineraries, routes: this.routes}

      this.itinerariesByUserListener.next(body)
    })

    /*
        function   getMoreInfo(itineraries: any) {
          let xids: any = []
          console.log(itineraries)
          itineraries.forEach((itinerary: any) => {
            itinerary.forEach((point: any) => {
              xids.push(point.xid)
            })
          })

          console.log("XIDS ", xids)
          var unique = [...new Set(xids)]
          console.log(unique)
          let delay = 0;
          unique.forEach((xid: any) => {
            processItemWithDelay(delay)
            this.pointOfInterestService.getPointOfInterestByXid(xid).subscribe((res: any) => {
              console.log("xid", xid, res)
              delay += 1000;
            })
          })


        }*/

    function processItemWithDelay(delay: any) {
      setTimeout(() => {
      }, delay);
    }

    function createPath(itinerary: any, route: any) {
      var origin = {};
      var originInfoWindow = {}

      var destination = {};
      var destinationWindow = {}

      var waypoints = [];
      var waypointInfoWindow = []
      for (let i = 0; i < itinerary.length; i++) {
        if (i === 0) {
          origin = {lat: itinerary[i].latitude, lng: itinerary[i].longitude}
          originInfoWindow = {
            visible: false,
            //infoWindow: itinerary[i].pointOfInterestName
          }
        } else if (i === itinerary.length - 1) {
          destination = {lat: itinerary[i].latitude, lng: itinerary[i].longitude}
          destinationWindow = {
            //infoWindow: itinerary[i].pointOfInterestName
          }
        } else {
          waypoints.push({
            location: {lat: itinerary[i].latitude, lng: itinerary[i].longitude},

          })
          waypointInfoWindow.push({
            //infoWindow:itinerary[i].infoWindow
          })
        }
      }

      var body = {
        itinerary_id: itinerary[0].itinerary_id,
        origin: origin,
        destination: destination,
        waypoints: waypoints,
        renderOptions: {
          suppressMarkers: true,
        },
      }

      return route(body)
    }

  }

  getItinerariesByUserListenerUpdated() {
    return this.itinerariesByUserListener.asObservable();
  }

  /**
   * Generate an itinerary given chosen points of interest
   *
   * @param
   */
  generateItinerary(itinerary: bodyItinerary[]) {
    return this.http.post(this.baseUrl + `/pointOfInterestItinerary/itinerary`, itinerary)

  }

  /**
   * Save itinerary given a list of points of a certain itinerary
   *
   * @param
   */
  saveItinerary(saveItinerary: any) {
    return this.http.post(this.baseUrl + `/pointOfInterestItinerary/itinerary/save`, saveItinerary)
  }

  /**
   *Delete an itinerary
   *
   * @param itinerary_id 1
   * @param user_id 4
   */
  deleteItinerary(itinerary_id: number, user_id: number) {
    console.log("Aqui ", itinerary_id, user_id)
    this.http.delete(this.baseUrl + `/pointOfInterestItinerary/itinerary/${user_id}/${itinerary_id}`).subscribe((resDeleted) => {

      this.routes = this.routes.filter((item: any) => {
        return item.itinerary_id !== itinerary_id
      });

      console.log("Antes ", this.itineraries)
      this.itineraries = this.itineraries.filter((item: any) => {
        return item.itinerary_id !== itinerary_id
      })
      console.log("dEOIUS ", this.itineraries)


      var body = {
        itineraries: this.itineraries,
        routes: this.routes
      }

      console.log(body)
      this.itinerariesByUserListener.next(body);

    })
  }

  /**
   * Delete a certain point of interest of a certain itinerary
   *
   * @param itinerary_id 1
   * @param user_id 6
   * @param name "W456"
   */
  deletePointOfInteresFromItinerary(itinerary_id: number, user_id: number, name: string) {
    this.http.delete(this.baseUrl + `/pointOfInterestItinerary/itinerary/${user_id}/${itinerary_id}/${name}`)
  }


  getGeneratedItinerary(itinerary: any) {
    this.generatedItineraryListener.next(itinerary)
  }

  getGeneratedItineraryUpdated() {
    return this.generatedItineraryListener.asObservable();
  }


}
