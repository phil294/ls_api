<template lang="slm">
li
	read-more :value=true :noliststyle=!category.children_ref.length
		template #summary=""
			.label :id="'tree-'+category.name" :class.empty="category.products_count===0"
				.wrapper.capitalize v-if=category.wrapper
					| $category.label
				router-link.capitalize v-else="" :to="'/list/'+category.name"
					| $category.label
		ul
			category-tree-item v-for="child of category.children_ref" :category=child :key=child.name
</template>

<script lang="coffee">
import { mapState, mapGetters } from 'vuex'

export default
	name: 'category-tree-item'
	props:
		category:
			required: true
			type: Object
</script>

<style lang="stylus" scoped>
.label.empty
	&, > a
		color var(--color-disabled)
</style>
