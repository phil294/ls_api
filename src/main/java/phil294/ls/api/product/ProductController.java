package phil294.ls.api.product;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phil294.ls.api.model.*;

import javax.validation.Valid;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;

/**
 * User: phi
 * Date: 10.03.17
 * .  .___.
 * .  {o,o}
 * . /)___)
 * . --"-"--
 */
@RestController
@RequestMapping("/product")
public class ProductController
{
	@Autowired
	private AttributeRepository attributeRepository;
	
	///////////////////////////////////////////
	//////////////// ADMIN FUNCTIONS //////////
	///////////////////////////////////////////
	
	/**
	 * Produkt hinzufügen
	 * @param user
	 * @param input
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Product> addProduct(
			@RequestAttribute("user") User user,
			@RequestBody @Valid Product input
	)
	{
		if( ! user.getAdmin()) {
			return new ResponseEntity<Product>(HttpStatus.UNAUTHORIZED);
		}
		Product product = new Product();
		product.setName(input.getName());
		product.setDescription(input.getDescription());
		product.setPicture(input.getPicture());
		
		product.setUser(user.getId());
		Document newProductDocument = Product.toDocument(product);
		MongoInstance.getProductCollection().insertOne(
				newProductDocument
		);
		product.setId(((ObjectId) newProductDocument.get("_id")).toHexString()); // damit frontend gleich die richtige id mit bekommt
		
		return new ResponseEntity<>(product, HttpStatus.OK);
	}
	
	/**
	 * Produkt bearbeiten (schließt ProductValues NICHT ein)
	 * @param user
	 * @param input
	 * @param productId
	 * @return
	 */
	@PutMapping("/{productId}")
	public ResponseEntity<Product> updateProduct(
			@RequestAttribute("user") User user,
			@RequestBody @Valid Product input,
			@PathVariable("productId") String productId
	)
	{
		if( ! user.getAdmin()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Product product = new Product();
		product.setName(input.getName()); // duplicate code
		product.setDescription(input.getDescription());
		product.setPicture(input.getPicture());
		//product.setProductData(input.getProductData());
		
		product.setId(productId);
		product.setUser(user.getId());
		MongoInstance.getProductCollection().updateOne(
				eq("_id", new ObjectId(productId)),
				//set(Product.toDocument(product)) //?
				new Document("$set", Product.toDocument(product))
		);
		return new ResponseEntity<>(product, HttpStatus.OK);
	}
	
	/**
	 * ProductValue bearbeiten
	 * @param user
	 * @param productId
	 * @param attributeId
	 * @param newValue
	 * @return
	 */
	@PutMapping("/{productId}/attribute/{attributeId}")
	public ResponseEntity<ProductValue> updateProductValue(
			@RequestAttribute("user") User user,
			@PathVariable("productId") String productId,
			@PathVariable("attributeId") Integer attributeId,
			@RequestBody String newValue
	)
	{
		if( ! user.getAdmin()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(newValue.isEmpty()) {
			throw new IllegalArgumentException("New value cannot be empty."); // (s. deleteProductValue)
		}
		MongoInstance.getProductCollection().updateOne(
				eq("_id", new ObjectId(productId)),
				set("productData." + attributeId + ".value", newValue));
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * ProductValue löschen
	 * @param user
	 * @param productId
	 * @param attributeId
	 * @return
	 */
	@DeleteMapping("/{productId}/attribute/{attributeId}")
	public ResponseEntity deleteProductValue(
			@RequestAttribute("user") User user,
			@PathVariable("productId") String productId,
			@PathVariable("attributeId") Integer attributeId
	)
	{
		if(!user.getAdmin()) {
			return new ResponseEntity<Product>(HttpStatus.UNAUTHORIZED);
		}
		MongoInstance.getProductCollection().updateOne(
				eq("_id", new ObjectId(productId)),
				// todo unset attrId oder attrId.value, was macht mehr sinn?
				unset("productData." + attributeId + ".value")
		);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * Produkt löschen.
	 * @param user
	 * @param productId
	 * @return
	 */
	@DeleteMapping("/{productId}")
	public ResponseEntity deleteProduct(
			@RequestAttribute("user") User user,
			@PathVariable("productId") String productId
	)
	{
		if( ! user.getAdmin()) {
			return new ResponseEntity<Product>(HttpStatus.UNAUTHORIZED);
		}
		MongoInstance.getProductCollection().deleteOne(
				eq("_id", new ObjectId(productId))
		);
		return new ResponseEntity(HttpStatus.OK);
	}
}
