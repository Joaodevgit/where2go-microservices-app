import {Component, OnInit} from '@angular/core';
import {SearchHistoryService} from "../services/search-history.service";
import {Router} from "@angular/router";
import {AuthService} from "../services/auth.service";
import {Subject, Subscription} from "rxjs";

@Component({
  selector: 'app-search-history',
  templateUrl: './search-history.component.html',
  styleUrls: ['./search-history.component.css']
})
export class SearchHistoryComponent implements OnInit {
  currentUser:any;
  searches: any;

  dadosAgrupados: any;



  pointSubs: Subscription;
  Object: any;

  constructor(private router: Router,
              private authService: AuthService,
              private searchHistoryService: SearchHistoryService) {
    this.currentUser= this.authService.getUser();
  }
  ngOnInit(): void {
    this.searchHistoryService.getLast5SearchesByUser(this.currentUser.id).subscribe((userSearches)=>{
      console.log("Pesquisas:::::", userSearches)
      this.searches=userSearches


      this.dadosAgrupados = this.searches.reduce((grupos:any, pesquisa:any) => {
        const search_id = pesquisa.search_id;
        const xid = pesquisa.xid;
        const name=pesquisa.name;
        if (!grupos[search_id]) {
          grupos[search_id] = [];
        }
        grupos[search_id].push({xid,name});
        return grupos;
      }, {});

      console.log("gruposssss::::::::::::", this.dadosAgrupados)

    })



  }


  getPointInterestInfo(xid: any) {
    console.log("Quero ver detalhes ", xid)
    this.router.navigate([`pointOfInterest/${xid}`]);
  }
}
