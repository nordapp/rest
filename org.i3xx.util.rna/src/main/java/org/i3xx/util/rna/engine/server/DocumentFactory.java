package org.i3xx.util.rna.engine.server;

import org.i3xx.util.rna.core.IDocumentStore;
import org.i3xx.util.rna.core.IIdsDocument;

public class DocumentFactory {

	public static final IDocumentStore getStore() {
		return new DummyStore();
	}
	
	private static class DummyStore implements IDocumentStore {
		
		public DummyStore() {
			
		}

		@Override
		public String migrate(IIdsDocument doc) {
			return "local:";
		}

		@Override
		public IIdsDocument fetch(String uri) {
			return null;
		}
	}
	
}
