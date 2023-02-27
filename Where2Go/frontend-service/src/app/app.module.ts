import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HeaderComponent} from './header/header.component';
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatTabsModule} from "@angular/material/tabs";
import {SidebarComponent} from './sidebar/sidebar.component';
import {MatSidenavModule} from "@angular/material/sidenav";
import {HomePageComponent} from "./home-page/home-page.component"
import {MatListModule} from "@angular/material/list";
import {MatButtonModule} from "@angular/material/button";
import {AuthComponent} from './auth/auth.component';
import {MatDialogModule} from "@angular/material/dialog";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatRadioModule} from "@angular/material/radio";
import {PointOfInterestComponent} from './point-of-interest/point-of-interest.component';
import {LoginComponent} from './auth/login/login.component';
import {MatCardModule} from "@angular/material/card";
import {MatInputModule} from '@angular/material/input';
import {RegisterComponent} from './auth/register/register.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {NotificationComponent} from './notification/notification.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {HttpClientModule} from "@angular/common/http";
import {FlexLayoutModule, FlexModule} from '@angular/flex-layout';
import {MatChipsModule} from '@angular/material/chips';
import { StarRatingComponent } from './star-rating/star-rating.component';
import { GenerateItineraryComponent } from './generate-itinerary/generate-itinerary.component';
import {MatBadgeModule} from '@angular/material/badge';
import { MyItineraryComponent } from './my-itinerary/my-itinerary.component';
import { AgmCoreModule } from '@agm/core';
import {AgmDirectionModule} from "agm-direction";
import { MyFavoritesComponent } from './my-favorites/my-favorites.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { PointOfInterestSearchComponent } from './point-of-interest-search/point-of-interest-search.component';
import { SearchHistoryComponent } from './search-history/search-history.component';
import {MatExpansionModule} from "@angular/material/expansion";
import { MatConfirmDialogComponent } from './mat-confirm-dialog/mat-confirm-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SidebarComponent,
    HomePageComponent,
    AuthComponent,
    PointOfInterestComponent,
    AuthComponent,
    LoginComponent,
    RegisterComponent,
    NotificationComponent,
    StarRatingComponent,
    GenerateItineraryComponent,
    MyItineraryComponent,
    MyFavoritesComponent,
    PointOfInterestSearchComponent,
    SearchHistoryComponent,
    MatConfirmDialogComponent,


  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatToolbarModule,
    MatMenuModule,
    MatTabsModule,
    MatSidenavModule,
    MatListModule,
    MatButtonModule,
    MatDialogModule,
    FormsModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatRadioModule,
    MatCardModule,
    MatInputModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    HttpClientModule,
    FlexModule,
    FlexLayoutModule,
    MatChipsModule,
    AgmCoreModule.forRoot({
      apiKey: "AIzaSyDn8y6OijIttcqIyxKZuAtpv_Lbi_ReKmM"
    }),
    MatBadgeModule,
    AgmDirectionModule,
    MatGridListModule,
    MatExpansionModule,
    MatGridListModule,
    AgmDirectionModule,
    AgmDirectionModule,

  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {
}
