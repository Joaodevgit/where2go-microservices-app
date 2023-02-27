import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomePageComponent} from "./home-page/home-page.component";
import {PointOfInterestComponent} from "./point-of-interest/point-of-interest.component";
import {GenerateItineraryComponent} from "./generate-itinerary/generate-itinerary.component";
import {MyFavoritesComponent} from "./my-favorites/my-favorites.component";
import {PointOfInterestSearchComponent} from "./point-of-interest-search/point-of-interest-search.component";
import {SearchHistoryComponent} from "./search-history/search-history.component";
import {MyItineraryComponent} from "./my-itinerary/my-itinerary.component";

const routes: Routes = [
  {path:'', component: HomePageComponent},
  {path:'pointOfInterest/:id', component: PointOfInterestComponent},
  {path: 'generate-itinerary', component: GenerateItineraryComponent},
  {path: 'myFavorites', component: MyFavoritesComponent},
  {path:'search/:idUser/:city', component: PointOfInterestSearchComponent},
  {path:'search/:idUser/:city/:radius/:kind/:place', component: PointOfInterestSearchComponent},
  {path: 'searchHistory', component: SearchHistoryComponent},
  {path: 'myItinerary', component: MyItineraryComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
