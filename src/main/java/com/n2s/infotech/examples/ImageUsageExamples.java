package com.n2s.infotech.examples;

import org.springframework.stereotype.Component;

/**
 * Exemples d'utilisation du système de gestion d'images
 *
 * Ce fichier contient des exemples de code pour :
 * - Créer un produit avec des images
 * - Mettre à jour les images d'un produit
 * - Supprimer des images
 */
@Component
public class ImageUsageExamples {

    /*
     * EXEMPLE 1 : Créer un produit avec upload d'images depuis le frontend
     *
     * Frontend (Angular/TypeScript) :
     *
     * async createProductWithImages(productData: any, imageFiles: File[]) {
     *   // 1. Upload des images
     *   const formData = new FormData();
     *   imageFiles.forEach(file => formData.append('files', file));
     *   formData.append('folder', 'products');
     *
     *   const uploadRes = await this.http.post<any>(
     *     '/api/images/upload/multiple',
     *     formData
     *   ).toPromise();
     *
     *   // 2. Créer le produit avec les URLs des images
     *   const product = {
     *     ...productData,
     *     imageUrls: uploadRes.uploaded.map(img => img.path)
     *   };
     *
     *   return this.http.post('/api/admin/products', product).toPromise();
     * }
     */

    /*
     * EXEMPLE 2 : Backend - Créer un produit avec des images
     *
     * Dans ProductAdminController, vous pouvez créer un endpoint comme :
     *
     * @PostMapping("/with-images")
     * public ProductDto createWithImages(
     *     @RequestPart("product") CreateProductRequest req,
     *     @RequestPart("images") MultipartFile[] images
     * ) {
     *     // 1. Créer le produit
     *     Product product = createProductFromRequest(req);
     *
     *     // 2. Uploader les images
     *     for (MultipartFile image : images) {
     *         String path = imageStorageService.store(image, "products");
     *
     *         ProductImage productImage = ProductImage.builder()
     *             .url(path)
     *             .altText(product.getTitle())
     *             .product(product)
     *             .build();
     *
     *         product.getImages().add(productImage);
     *     }
     *
     *     // 3. Sauvegarder
     *     product = productRepository.save(product);
     *
     *     return convertToDto(product);
     * }
     */

    /*
     * EXEMPLE 3 : Frontend - Composant Angular pour upload d'images
     *
     * // image-uploader.component.ts
     * export class ImageUploaderComponent {
     *   selectedFiles: File[] = [];
     *   uploadedImages: any[] = [];
     *
     *   onFileSelect(event: any) {
     *     this.selectedFiles = Array.from(event.target.files);
     *   }
     *
     *   async uploadImages() {
     *     const formData = new FormData();
     *     this.selectedFiles.forEach(file => {
     *       formData.append('files', file);
     *     });
     *
     *     const result = await this.http.post(
     *       '/api/images/upload/multiple',
     *       formData
     *     ).toPromise();
     *
     *     this.uploadedImages = result.uploaded;
     *   }
     *
     *   removeImage(index: number) {
     *     const image = this.uploadedImages[index];
     *     this.http.delete(`/api/images?path=${image.path}`)
     *       .subscribe(() => {
     *         this.uploadedImages.splice(index, 1);
     *       });
     *   }
     * }
     *
     * // image-uploader.component.html
     * <div class="image-uploader">
     *   <input
     *     type="file"
     *     multiple
     *     accept="image/*"
     *     (change)="onFileSelect($event)"
     *   />
     *
     *   <button (click)="uploadImages()">
     *     Uploader les images
     *   </button>
     *
     *   <div class="preview-grid">
     *     <div *ngFor="let img of uploadedImages; let i = index" class="preview-item">
     *       <img [src]="img.url" alt="Preview">
     *       <button (click)="removeImage(i)">Supprimer</button>
     *     </div>
     *   </div>
     * </div>
     */

    /*
     * EXEMPLE 4 : Service Angular pour gérer les images
     *
     * // image.service.ts
     * @Injectable({ providedIn: 'root' })
     * export class ImageService {
     *   private apiUrl = 'http://localhost:8080/api/images';
     *
     *   constructor(private http: HttpClient) {}
     *
     *   uploadSingle(file: File, folder: string = 'products'): Observable<any> {
     *     const formData = new FormData();
     *     formData.append('file', file);
     *     formData.append('folder', folder);
     *
     *     return this.http.post(`${this.apiUrl}/upload`, formData);
     *   }
     *
     *   uploadMultiple(files: File[], folder: string = 'products'): Observable<any> {
     *     const formData = new FormData();
     *     files.forEach(file => formData.append('files', file));
     *     formData.append('folder', folder);
     *
     *     return this.http.post(`${this.apiUrl}/upload/multiple`, formData);
     *   }
     *
     *   uploadWithThumbnail(file: File, folder: string = 'products'): Observable<any> {
     *     const formData = new FormData();
     *     formData.append('file', file);
     *     formData.append('folder', folder);
     *
     *     return this.http.post(`${this.apiUrl}/upload/with-thumbnail`, formData);
     *   }
     *
     *   delete(path: string): Observable<any> {
     *     return this.http.delete(`${this.apiUrl}?path=${path}`);
     *   }
     * }
     */

    /*
     * EXEMPLE 5 : Composant complet de création de produit avec images
     *
     * // product-create.component.ts
     * export class ProductCreateComponent {
     *   productForm: FormGroup;
     *   selectedImages: File[] = [];
     *   previewUrls: string[] = [];
     *
     *   constructor(
     *     private fb: FormBuilder,
     *     private imageService: ImageService,
     *     private productService: ProductService
     *   ) {
     *     this.productForm = this.fb.group({
     *       title: ['', Validators.required],
     *       description: [''],
     *       brand: [''],
     *       model: [''],
     *       condition: ['NEW'],
     *       categoryId: [null]
     *     });
     *   }
     *
     *   onImagesSelected(event: any) {
     *     const files: File[] = Array.from(event.target.files);
     *
     *     // Limiter à 5 images max
     *     this.selectedImages = files.slice(0, 5);
     *
     *     // Générer des previews
     *     this.previewUrls = [];
     *     this.selectedImages.forEach(file => {
     *       const reader = new FileReader();
     *       reader.onload = (e: any) => {
     *         this.previewUrls.push(e.target.result);
     *       };
     *       reader.readAsDataURL(file);
     *     });
     *   }
     *
     *   async submit() {
     *     if (!this.productForm.valid) return;
     *
     *     try {
     *       // 1. Upload des images
     *       let imageUrls = [];
     *       if (this.selectedImages.length > 0) {
     *         const uploadResult = await this.imageService
     *           .uploadMultiple(this.selectedImages)
     *           .toPromise();
     *
     *         imageUrls = uploadResult.uploaded.map(img => img.path);
     *       }
     *
     *       // 2. Créer le produit
     *       const productData = {
     *         ...this.productForm.value,
     *         imageUrls: imageUrls
     *       };
     *
     *       await this.productService.create(productData).toPromise();
     *
     *       // Rediriger vers la liste des produits
     *       this.router.navigate(['/products']);
     *
     *     } catch (error) {
     *       console.error('Erreur lors de la création du produit', error);
     *     }
     *   }
     * }
     */

    /*
     * EXEMPLE 6 : Drag & Drop pour upload d'images
     *
     * // drag-drop-uploader.component.ts
     * export class DragDropUploaderComponent {
     *   isDragging = false;
     *   uploadedImages: any[] = [];
     *
     *   onDragOver(event: DragEvent) {
     *     event.preventDefault();
     *     this.isDragging = true;
     *   }
     *
     *   onDragLeave(event: DragEvent) {
     *     event.preventDefault();
     *     this.isDragging = false;
     *   }
     *
     *   async onDrop(event: DragEvent) {
     *     event.preventDefault();
     *     this.isDragging = false;
     *
     *     const files = Array.from(event.dataTransfer?.files || []);
     *     const imageFiles = files.filter(file =>
     *       file.type.startsWith('image/')
     *     );
     *
     *     if (imageFiles.length > 0) {
     *       const result = await this.imageService
     *         .uploadMultiple(imageFiles as File[])
     *         .toPromise();
     *
     *       this.uploadedImages.push(...result.uploaded);
     *     }
     *   }
     * }
     *
     * // drag-drop-uploader.component.html
     * <div
     *   class="dropzone"
     *   [class.dragging]="isDragging"
     *   (dragover)="onDragOver($event)"
     *   (dragleave)="onDragLeave($event)"
     *   (drop)="onDrop($event)"
     * >
     *   <p>Glissez-déposez vos images ici</p>
     *   <p>ou</p>
     *   <input type="file" multiple accept="image/*" (change)="onFileSelect($event)">
     * </div>
     *
     * // styles
     * .dropzone {
     *   border: 2px dashed #ccc;
     *   border-radius: 8px;
     *   padding: 40px;
     *   text-align: center;
     *   transition: all 0.3s;
     * }
     *
     * .dropzone.dragging {
     *   border-color: #4CAF50;
     *   background-color: #f1f8f4;
     * }
     */
}

