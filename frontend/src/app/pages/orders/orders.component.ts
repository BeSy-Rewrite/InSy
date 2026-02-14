
import { Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AccordionComponent } from '../../components/accordion/accordion.component';
import { CardComponent } from '../../components/card/card.component';
import { DialogData, setupDialog } from '../../components/dialog/dialog.component';
import { Article } from '../../models/Article';
import { Order, ORDER_ID_ARTICLE_ID_SEPARATOR } from '../../models/Order';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-orders',
  imports: [
    CardComponent,
    AccordionComponent,
    MatButtonModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatExpansionModule,
    MatIconModule
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {

  constructor(private readonly router: Router,
    private readonly orderService: OrderService,
    private readonly dialog: MatDialog) {
  }

  // Orders
  orders: Order[] = [];
  numberOfArticles = () => {
    return this.orders.flatMap(order => order.articles).length;
  };
  checkedArticles: Article[] = [];

  // get all active orders
  ngOnInit(): void {
    // Check for new articles in orders when the user navigates to this page
    this.orderService.openArticlesChanged.next();
    // Fetch orders and add checked attribute to each article, to track which orders the user has selected for inventorization
    this.orderService.getOpenOrders().subscribe(orders => {
      this.orders = orders.map(order => ({
        ...order,
        articles: order.articles.map(article => ({
          ...article,
          checked: false
        }))
      }));

      this.orders.forEach(order => {
        order.articles = order.articles.filter(article => !article.is_inventoried);
      });

    });

  }

  getBesyOrderUrl(order: Order) {
    return `${environment.besyUrl}/orders/${order.besy_id}`;
  }

  // Accordion
  @ViewChildren(AccordionComponent) accordions!: QueryList<AccordionComponent>;

  openAllAccordion() {
    this.accordions.map((accordion: AccordionComponent) => {
      accordion.matAccordion.openAll();
    });
  }

  closeAllAccordions() {
    this.accordions.map((accordion: AccordionComponent) => {
      accordion.matAccordion.closeAll();
    });
  }

  // This will stop the Accordion from expanding when the checkbox is clicked
  // and check or uncheck the clicked check box
  checkBox(event: MouseEvent | KeyboardEvent, article: any) {
    if (event instanceof KeyboardEvent && event.key !== 'Enter' && event.key !== ' ') {
      return;
    }
    event.stopPropagation();
    article.checked = !article.checked;
    this.allChecked = this.isAllBoxesChecked();
    if (article.checked) {
      this.checkedArticles.push(article);
    } else {
      this.checkedArticles = this.checkedArticles.filter(a => a.article_id !== article.article_id);
    }
  }

  // tracks how many articles are checked
  checkedCount = () => {
    return this.orders.flatMap(order => order.articles).filter(article => article.checked).length;
  };

  // "Alle auswählen" checkbox
  allChecked = false;

  // Returns whether all check boxes are checked
  isAllBoxesChecked(): boolean {
    let allChecked = true;
    this.orders.forEach(order => {
      order.articles.forEach(article => {
        if (!article.checked) {
          allChecked = false;
        }
      });
    });
    this.checkedCount();
    return allChecked;
  }

  // Check/uncheck all check boxes
  checkAll() {
    this.allChecked = !this.allChecked;
    this.orders.forEach(order => {
      order.articles.forEach(article => {
        article.checked = this.allChecked;
        if (article.checked && !this.checkedArticles.includes(article)) {
          this.checkedArticles.push(article);
        }
      });
    });
    this.checkedCount();
  }

  isOrderChecked(order: Order): boolean {
    return order.articles.every(article => article.checked);
  }

  checkOrder(event: MouseEvent | KeyboardEvent, order: Order) {
    if (event instanceof KeyboardEvent && event.key !== 'Enter' && event.key !== ' ') {
      return;
    }
    event.stopPropagation();
    const isChecked = this.isOrderChecked(order);
    order.articles.forEach(article => {
      article.checked = !isChecked;
      if (article.checked && !this.checkedArticles.includes(article)) {
        this.checkedArticles.push(article);
      } else if (!article.checked) {
        this.checkedArticles = this.checkedArticles.filter(a => a.article_id !== article.article_id);
      }
    });
    this.allChecked = this.isAllBoxesChecked();
  }


  // "Jetzt inventarisieren" Button'

  private getArticleOrderId(article: Article): number {
    const order = this.orders.find(order => order.articles.includes(article));
    return order ? order.id : -1;
  }

  private collectArticles(articles: Article[]): string[] {
    return articles.map(article => `${this.getArticleOrderId(article)}${ORDER_ID_ARTICLE_ID_SEPARATOR}${article.article_id}`);
  }

  /**
   * Inventarize the checked articles in the selected mode.
   * - asItems: Inventarize all checked articles as items.
   * - asExtensions: Inventarize all checked articles as extensions.
   * - addToFirst: Inventarize the first checked article as an item and the rest as extensions of the first article.
   * @param mode - The mode of inventorization: 'asItems', 'asExtensions', or 'addToFirst'.
   */
  inventarize(mode: string) {
    if (this.checkedArticles.length < 1) {
      return;
    }

    let itemArticles: string[] = [];
    let extensionArticles: string[] = [];
    let route: string[] = [];

    switch (mode) {
      case 'asItems':
        itemArticles = this.collectArticles(this.checkedArticles);
        route.push('/new');
        break;
      case 'asExtensions':
        extensionArticles = this.collectArticles(this.checkedArticles);
        route.push('/new-extension');
        break;
      case 'addToFirst':
        if (this.checkedArticles.length >= 2) {
          itemArticles = this.collectArticles([this.checkedArticles[0]]);
          extensionArticles = this.collectArticles(this.checkedArticles.slice(1));
        }
        route.push('/new');
        break;
      default:
        console.error('Unknown mode:', mode);
    }

    this.router.navigate(route, { queryParams: { itemArticles, extensionArticles } });
  }

  deleteCheckedArticles() {
    if (this.checkedArticles.length < 1) {
      return;
    }

    const articleObservables: Observable<void>[] = [];

    for (const article of this.checkedArticles) {
      const orderId = this.getArticleOrderId(article);
      if (orderId !== -1) {
        articleObservables.push(this.orderService.deleteOrderArticle(orderId, article.article_id));
      }
    }

    forkJoin(articleObservables).subscribe(() => {
      this.checkedArticles = [];
      this.allChecked = false;
      this.ngOnInit();
    });
  }

  openDeleteDialog() {
    const data: DialogData = {
      title: 'Ausgewählte Artikel löschen?',
      description: 'Möchten Sie alle ausgewählten Artikel wirklich löschen?',
      confirmButtonText: 'Löschen',
      cancelButtonText: 'Abbrechen'
    };

    setupDialog(this.dialog, data, (result) => {
      if (result) {
        this.deleteCheckedArticles();
      }
    });
  }

}
