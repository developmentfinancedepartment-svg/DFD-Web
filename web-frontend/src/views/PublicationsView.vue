<template>
  <div class="publications-page">
    <!-- Hero Header -->
    <header class="hero-header">
      <div class="pub-container text-center">
        <h1 class="hero-title">{{ $t('publications.heroTitle') }}</h1>
        <p class="hero-subtitle">
          {{ $t('publications.heroSubtitle') }}
        </p>
      </div>
    </header>

    <!-- Filter Bar -->
    <section class="pub-filter-bar">
      <div class="pub-container filter-flex-wrapper">
        <!-- Category Summary Tabs -->
        <div class="filter-tabs-group">
          <button
            type="button"
            @click="selectedCategoryFilter = 'ALL'"
            class="filter-pill-btn"
            :class="{ 'filter-pill-active': selectedCategoryFilter === 'ALL' }"
          >
            <span>All Documents</span>
            <span class="count-tag">{{ allCount }}</span>
          </button>

          <button
            type="button"
            @click="selectedCategoryFilter = 'ACTION_PLAN'"
            class="filter-pill-btn"
            :class="{ 'filter-pill-active': selectedCategoryFilter === 'ACTION_PLAN' }"
          >
            <span>Action Plans</span>
            <span class="count-tag">{{ totalActionPlansCount }}</span>
          </button>

          <button
            type="button"
            @click="selectedCategoryFilter = 'ANNUAL_REPORT'"
            class="filter-pill-btn"
            :class="{ 'filter-pill-active': selectedCategoryFilter === 'ANNUAL_REPORT' }"
          >
            <span>Annual Reports</span>
            <span class="count-tag">{{ totalAnnualReportsCount }}</span>
          </button>

          <button
            v-if="totalCircularsCount > 0"
            type="button"
            @click="selectedCategoryFilter = 'CIRCULAR'"
            class="filter-pill-btn"
            :class="{ 'filter-pill-active': selectedCategoryFilter === 'CIRCULAR' }"
          >
            <span>Circulars</span>
            <span class="count-tag">{{ totalCircularsCount }}</span>
          </button>
        </div>

        <div class="filter-right-group">
          <button
            v-if="searchQuery || selectedYear !== 'All' || selectedCategoryFilter !== 'ALL'"
            type="button"
            class="reset-filters-btn"
            @click="resetFilters"
          >
            <i class="fas fa-undo-alt mr-1"></i>
            {{ $t('common.resetFilters') }}
          </button>

          <!-- Year Filter Custom Dropdown -->
          <div class="filter-dropdown-area">
            <div class="filter-label">
              <svg class="filter-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M3 3a1 1 0 011-1h12a1 1 0 011 1v3a1 1 0 01-.293.707L12 11.414V15a1 1 0 01-.293.707l-2 2A1 1 0 018 17v-5.586L3.293 6.707A1 1 0 013 6V3z" clip-rule="evenodd" />
              </svg>
              <span>{{ $t('publications.filterYear') }}</span>
            </div>

            <div
              ref="dropdownRef"
              class="custom-dropdown"
              @mouseenter="isOpen = true"
              @mouseleave="isOpen = false"
            >
              <button
                type="button"
                class="dropdown-btn"
                :class="{ 'dropdown-btn-open': isOpen }"
                @click="toggleDropdown"
              >
                <span>{{ selectedYearLabel }}</span>
                <svg
                  class="chevron-icon"
                  :class="{ 'rotate-180': isOpen }"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2.5"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <!-- Dropdown Menu -->
              <transition name="dropdown-anim">
                <div v-if="isOpen" class="dropdown-menu-list">
                  <button
                    v-for="item in yearOptions"
                    :key="item.value"
                    type="button"
                    class="dropdown-item"
                    :class="{ 'item-selected': selectedYear === item.value }"
                    @click="selectYear(item.value)"
                  >
                    <span class="item-label-group">
                      <span v-if="selectedYear === item.value" class="active-dot"></span>
                      <span>{{ item.label }}</span>
                    </span>
                    <svg
                      v-if="selectedYear === item.value"
                      class="check-icon"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2.5"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                  </button>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content Section -->
    <main class="pub-content-section">
      <div class="pub-container">
        <!-- Loading State -->
        <div v-if="isLoading" class="state-container">
          <div class="loader-spinner"></div>
          <p class="state-text">Loading publications &amp; reports...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="state-container">
          <i class="fas fa-exclamation-triangle state-icon-warning"></i>
          <h4 class="state-text">{{ error }}</h4>
          <button class="btn-solid-teal" @click="fetchPublications">Try Again</button>
        </div>

        <!-- Sectioned Layout -->
        <template v-else-if="hasAnyResults">
          <!-- SECTION 1: Action Plans -->
          <section v-if="showActionPlansSection && actionPlans.length > 0" class="pub-type-section">
            <div class="pub-section-header">
              <div class="pub-section-title-wrap">
                <div class="pub-section-icon teal">
                  <i class="fas fa-tasks"></i>
                </div>
                <div class="pub-section-headings">
                  <div class="pub-section-title-row">
                    <h2 class="pub-section-title">Action Plans</h2>
                    <span class="pub-section-count teal">{{ actionPlans.length }}</span>
                  </div>
                  <p class="pub-section-desc">Strategic departmental roadmaps and operational frameworks</p>
                </div>
              </div>
              <div class="pub-section-accent-line"></div>
            </div>

            <!-- ITMD 4-Column Responsive Grid -->
            <div class="pub-card-grid">
              <div
                v-for="doc in actionPlans"
                :key="doc.id"
                class="pub-card"
                @click="openLanguageModal(doc)"
              >
                <!-- Top Cover Preview Area (aspect-[4/3] / h-44) -->
                <div class="pub-card-cover">
                  <img
                    v-if="doc.coverImageUrl"
                    :src="getMediaUrl(doc.coverImageUrl)"
                    alt="Publication Cover"
                    class="pub-card-cover-img"
                  />
                  <!-- Sleek Branded Fallback Cover -->
                  <div v-else class="branded-cover">
                    <div class="cover-pattern-bg"></div>
                    <div class="cover-top-row">
                      <span class="cover-emblem-badge">
                        <i class="fas fa-landmark text-xs"></i>
                        <span>DFD</span>
                      </span>
                      <span class="cover-year-badge">{{ doc.year }}</span>
                    </div>

                    <div class="cover-center-content">
                      <p class="cover-category-tag">ACTION PLAN</p>
                      <h4 class="cover-doc-title">
                        {{ doc.title }}
                      </h4>
                    </div>

                    <div class="cover-footer-row">
                      <span>Ministry of Finance</span>
                      <i class="fas fa-file-pdf text-teal-300"></i>
                    </div>
                  </div>

                  <!-- Hover overlay preview indicator -->
                  <div class="pub-cover-hover-overlay">
                    <span class="pub-cover-hover-pill">
                      <i class="fas fa-file-pdf"></i>
                      <span>View Editions</span>
                    </span>
                  </div>
                </div>

                <!-- Card Body -->
                <div class="pub-card-body">
                  <div class="pub-card-top-content">
                    <div class="pub-card-meta">
                      <span class="pub-card-date">
                        <i class="far fa-calendar-alt"></i>
                        {{ formatDateDisplay(doc.releaseDate, doc.year) }}
                      </span>
                      <!-- Language Pills: EN, SI, TA -->
                      <div class="pub-lang-pills">
                        <span
                          v-for="lang in getAvailableLanguages(doc)"
                          :key="lang.code"
                          class="pub-lang-pill"
                          :class="`lang-${lang.code.toLowerCase()}`"
                          :title="`${lang.label} (${lang.size})`"
                        >
                          {{ lang.badgeLabel }}
                        </span>
                      </div>
                    </div>

                    <h3 class="pub-card-title" :title="doc.title">
                      {{ doc.title }}
                    </h3>

                    <p v-if="cleanDescription(doc.description)" class="pub-card-desc">
                      {{ cleanDescription(doc.description) }}
                    </p>
                  </div>

                  <!-- Full-width Action Button -->
                  <button
                    type="button"
                    @click.stop="openLanguageModal(doc)"
                    class="pub-card-action-btn"
                  >
                    <i class="fas fa-file-download"></i>
                    <span>View / Download Document</span>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <!-- SECTION 2: Annual Performance Reports -->
          <section v-if="showAnnualReportsSection && annualReports.length > 0" class="pub-type-section">
            <div class="pub-section-header">
              <div class="pub-section-title-wrap">
                <div class="pub-section-icon amber">
                  <i class="fas fa-chart-line"></i>
                </div>
                <div class="pub-section-headings">
                  <div class="pub-section-title-row">
                    <h2 class="pub-section-title">Annual Performance Reports</h2>
                    <span class="pub-section-count amber">{{ annualReports.length }}</span>
                  </div>
                  <p class="pub-section-desc">Comprehensive annual evaluations, credit disbursements, and institutional metrics</p>
                </div>
              </div>
              <div class="pub-section-accent-line amber-line"></div>
            </div>

            <!-- ITMD 4-Column Responsive Grid -->
            <div class="pub-card-grid">
              <div
                v-for="doc in annualReports"
                :key="doc.id"
                class="pub-card"
                @click="openLanguageModal(doc)"
              >
                <!-- Top Cover Preview Area (aspect-[4/3] / h-44) -->
                <div class="pub-card-cover">
                  <img
                    v-if="doc.coverImageUrl"
                    :src="getMediaUrl(doc.coverImageUrl)"
                    alt="Publication Cover"
                    class="pub-card-cover-img"
                  />
                  <!-- Sleek Branded Fallback Cover for Annual Reports -->
                  <div v-else class="branded-cover-annual">
                    <div class="cover-pattern-bg"></div>
                    <div class="cover-top-row">
                      <span class="cover-emblem-badge emblem-amber">
                        <i class="fas fa-landmark text-xs"></i>
                        <span>DFD</span>
                      </span>
                      <span class="cover-year-badge year-amber">{{ doc.year }}</span>
                    </div>

                    <div class="cover-center-content">
                      <p class="cover-category-tag tag-amber">ANNUAL REPORT</p>
                      <h4 class="cover-doc-title">
                        {{ doc.title }}
                      </h4>
                    </div>

                    <div class="cover-footer-row">
                      <span>Ministry of Finance</span>
                      <i class="fas fa-file-pdf text-amber-300"></i>
                    </div>
                  </div>

                  <!-- Hover overlay preview indicator -->
                  <div class="pub-cover-hover-overlay">
                    <span class="pub-cover-hover-pill">
                      <i class="fas fa-file-pdf"></i>
                      <span>View Editions</span>
                    </span>
                  </div>
                </div>

                <!-- Card Body -->
                <div class="pub-card-body">
                  <div class="pub-card-top-content">
                    <div class="pub-card-meta">
                      <span class="pub-card-date">
                        <i class="far fa-calendar-alt"></i>
                        {{ formatDateDisplay(doc.releaseDate, doc.year) }}
                      </span>
                      <!-- Language Pills: EN, SI, TA -->
                      <div class="pub-lang-pills">
                        <span
                          v-for="lang in getAvailableLanguages(doc)"
                          :key="lang.code"
                          class="pub-lang-pill"
                          :class="`lang-${lang.code.toLowerCase()}`"
                          :title="`${lang.label} (${lang.size})`"
                        >
                          {{ lang.badgeLabel }}
                        </span>
                      </div>
                    </div>

                    <h3 class="pub-card-title" :title="doc.title">
                      {{ doc.title }}
                    </h3>

                    <p v-if="cleanDescription(doc.description)" class="pub-card-desc">
                      {{ cleanDescription(doc.description) }}
                    </p>
                  </div>

                  <!-- Full-width Action Button -->
                  <button
                    type="button"
                    @click.stop="openLanguageModal(doc)"
                    class="pub-card-action-btn"
                  >
                    <i class="fas fa-file-download"></i>
                    <span>View / Download Document</span>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <!-- SECTION 3: Circulars & Policy Papers (Optional) -->
          <section v-if="showCircularsSection && circulars.length > 0" class="pub-type-section">
            <div class="pub-section-header">
              <div class="pub-section-title-wrap">
                <div class="pub-section-icon indigo">
                  <i class="fas fa-file-contract"></i>
                </div>
                <div class="pub-section-headings">
                  <div class="pub-section-title-row">
                    <h2 class="pub-section-title">Circulars &amp; Policy Papers</h2>
                    <span class="pub-section-count indigo">{{ circulars.length }}</span>
                  </div>
                  <p class="pub-section-desc">Statutory instructions, lending guidelines, and credit frameworks</p>
                </div>
              </div>
              <div class="pub-section-accent-line indigo-line"></div>
            </div>

            <!-- ITMD 4-Column Responsive Grid -->
            <div class="pub-card-grid">
              <div
                v-for="doc in circulars"
                :key="doc.id"
                class="pub-card"
                @click="openLanguageModal(doc)"
              >
                <!-- Top Cover Preview Area (aspect-[4/3] / h-44) -->
                <div class="pub-card-cover">
                  <img
                    v-if="doc.coverImageUrl"
                    :src="getMediaUrl(doc.coverImageUrl)"
                    alt="Publication Cover"
                    class="pub-card-cover-img"
                  />
                  <!-- Sleek Branded Fallback Cover for Circulars -->
                  <div v-else class="branded-cover-circular">
                    <div class="cover-pattern-bg"></div>
                    <div class="cover-top-row">
                      <span class="cover-emblem-badge emblem-indigo">
                        <i class="fas fa-landmark text-xs"></i>
                        <span>DFD</span>
                      </span>
                      <span class="cover-year-badge year-indigo">{{ doc.year }}</span>
                    </div>

                    <div class="cover-center-content">
                      <p class="cover-category-tag tag-indigo">CIRCULAR / POLICY</p>
                      <h4 class="cover-doc-title">
                        {{ doc.title }}
                      </h4>
                    </div>

                    <div class="cover-footer-row">
                      <span>Ministry of Finance</span>
                      <i class="fas fa-file-pdf text-indigo-300"></i>
                    </div>
                  </div>

                  <!-- Hover overlay preview indicator -->
                  <div class="pub-cover-hover-overlay">
                    <span class="pub-cover-hover-pill">
                      <i class="fas fa-file-pdf"></i>
                      <span>View Editions</span>
                    </span>
                  </div>
                </div>

                <!-- Card Body -->
                <div class="pub-card-body">
                  <div class="pub-card-top-content">
                    <div class="pub-card-meta">
                      <span class="pub-card-date">
                        <i class="far fa-calendar-alt"></i>
                        {{ formatDateDisplay(doc.releaseDate, doc.year) }}
                      </span>
                      <!-- Language Pills: EN, SI, TA -->
                      <div class="pub-lang-pills">
                        <span
                          v-for="lang in getAvailableLanguages(doc)"
                          :key="lang.code"
                          class="pub-lang-pill"
                          :class="`lang-${lang.code.toLowerCase()}`"
                          :title="`${lang.label} (${lang.size})`"
                        >
                          {{ lang.badgeLabel }}
                        </span>
                      </div>
                    </div>

                    <h3 class="pub-card-title" :title="doc.title">
                      {{ doc.title }}
                    </h3>

                    <p v-if="cleanDescription(doc.description)" class="pub-card-desc">
                      {{ cleanDescription(doc.description) }}
                    </p>
                  </div>

                  <!-- Full-width Action Button -->
                  <button
                    type="button"
                    @click.stop="openLanguageModal(doc)"
                    class="pub-card-action-btn"
                  >
                    <i class="fas fa-file-download"></i>
                    <span>View / Download Document</span>
                  </button>
                </div>
              </div>
            </div>
          </section>
        </template>

        <!-- Empty Results State (strictly mutually exclusive with loading) -->
        <div v-else-if="!isLoading && !hasAnyResults" class="empty-state-card">
          <div class="empty-state-icon-wrap">
            <i class="fas fa-search"></i>
          </div>
          <h4 class="empty-state-title">{{ $t('publications.noPubsTitle') }}</h4>
          <p class="empty-state-desc">{{ $t('publications.noPubsDesc') }}</p>
          <button class="btn-solid-teal" @click="resetFilters">
            {{ $t('common.resetFilters') }}
          </button>
        </div>
      </div>
    </main>

    <!-- 4. MULTI-LANGUAGE SELECTION MODAL -->
    <div
      v-if="showModal && selectedDoc"
      class="pub-modal-backdrop"
      @click.self="closeLanguageModal"
    >
      <div class="pub-modal-container">
        <!-- Modal Header -->
        <div class="pub-modal-header">
          <div class="pub-modal-header-left">
            <div class="pub-modal-badges">
              <span
                class="pub-modal-cat-badge"
                :class="getCategoryBadgeClass(selectedDoc.documentType || selectedDoc.category)"
              >
                {{ formatCategoryLabel(selectedDoc.documentType || selectedDoc.category) }}
              </span>
              <span class="pub-modal-year-badge">
                FY {{ selectedDoc.year }}
              </span>
            </div>
            <h3 class="pub-modal-title">
              {{ selectedDoc.title }}
            </h3>
            <p class="pub-modal-date">
              <i class="far fa-calendar-alt"></i>
              <span>Published: {{ formatDateDisplay(selectedDoc.releaseDate, selectedDoc.year) }}</span>
            </p>
          </div>
          <button
            type="button"
            @click="closeLanguageModal"
            class="pub-modal-close-btn"
            title="Close modal"
          >
            <i class="fas fa-times"></i>
          </button>
        </div>

        <!-- Description if available -->
        <div v-if="cleanDescription(selectedDoc.description)" class="pub-modal-desc-box">
          {{ cleanDescription(selectedDoc.description) }}
        </div>

        <!-- Language Editions List (Shows ONLY uploaded languages) -->
        <div class="pub-modal-languages-wrap">
          <p class="pub-modal-lang-heading">
            Available Language Editions ({{ modalLanguages.length }})
          </p>

          <div class="pub-modal-lang-list">
            <div
              v-for="lang in modalLanguages"
              :key="lang.code"
              class="pub-modal-lang-item"
            >
              <div class="pub-modal-lang-info">
                <span
                  class="pub-modal-lang-badge"
                  :class="`lang-${lang.code.toLowerCase()}`"
                >
                  {{ lang.code }}
                </span>
                <div class="pub-modal-lang-details">
                  <p class="pub-modal-lang-name">
                    {{ lang.label }}
                  </p>
                  <p class="pub-modal-lang-meta">
                    <span class="pub-modal-lang-native">{{ lang.nativeLabel }}</span>
                    <span class="dot-sep">•</span>
                    <span>{{ lang.size }}</span>
                  </p>
                </div>
              </div>

              <div class="pub-modal-lang-actions">
                <!-- View PDF Button (Opens in new tab) -->
                <a
                  :href="getMediaUrl(lang.url)"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="pub-btn-view"
                  title="View document in new tab"
                >
                  <i class="fas fa-eye"></i>
                  <span>View PDF</span>
                </a>

                <!-- Download Button -->
                <a
                  :href="getDownloadUrl(lang.url)"
                  :download="getFilenameForDownload(selectedDoc, lang)"
                  class="pub-btn-download"
                  title="Download PDF"
                >
                  <i class="fas fa-download"></i>
                  <span>Download</span>
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="pub-modal-footer">
          <button
            type="button"
            @click="closeLanguageModal"
            class="pub-modal-btn-close"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import axios from 'axios';
import BaseUrl from '@/services/baseURL';
import { navbarSearchState, clearNavbarSearch, setNavbarSearchQuery } from '@/services/navbarSearchState';

const route = useRoute();
const { t } = useI18n();
const publications = ref([]);
const isLoading = ref(true);
const error = ref(null);

const searchQuery = computed({
  get() {
    return navbarSearchState.query;
  },
  set(val) {
    setNavbarSearchQuery(val);
  }
});

const selectedYear = ref('All');
const selectedCategoryFilter = ref('ALL');
const isOpen = ref(false);
const dropdownRef = ref(null);

// Language Selection Modal State
const showModal = ref(false);
const selectedDoc = ref(null);

// Clean text helper to strip any citation artifacts (e.g. [1], , etc.)
const cleanDescription = (text) => {
  if (!text) return '';
  return String(text)
    .replace(/\[\^?\d+\]/g, '')
    .replace(/\u3010.*?\u3011/g, '')
    .trim();
};

// Fallback publications if backend is offline
const fallbackPublications = [
  {
    id: 1,
    title: 'Department Action Plan 2026',
    documentType: 'ACTION_PLAN',
    description: 'Comprehensive operational roadmap, division targets, and credit disbursement schedules for FY 2026.',
    year: '2026',
    releaseDate: '2026-01-15',
    pdfEnglishUrl: '/documents/action-plan-2026-en.pdf',
    pdfEnglishSize: '3.4 MB',
    pdfSinhalaUrl: '/documents/action-plan-2026-si.pdf',
    pdfSinhalaSize: '3.6 MB',
    pdfTamilUrl: '/documents/action-plan-2026-ta.pdf',
    pdfTamilSize: '3.5 MB'
  },
  {
    id: 2,
    title: 'Department Action Plan 2025',
    documentType: 'ACTION_PLAN',
    description: 'Annual institutional action plan outlining concessionary credit schemes and MSME revitalization targets.',
    year: '2025',
    releaseDate: '2025-01-10',
    pdfEnglishUrl: '/documents/action-plan-2025-en.pdf',
    pdfEnglishSize: '2.8 MB',
    pdfSinhalaUrl: '/documents/action-plan-2025-si.pdf',
    pdfSinhalaSize: '3.1 MB'
  },
  {
    id: 3,
    title: 'Department Action Plan 2024',
    documentType: 'ACTION_PLAN',
    description: 'Strategic action framework, performance indicators, and treasury credit line execution guidelines.',
    year: '2024',
    releaseDate: '2024-01-12',
    pdfEnglishUrl: '/documents/action-plan-2024-en.pdf',
    pdfEnglishSize: '2.5 MB'
  },
  {
    id: 4,
    title: 'Annual Performance Report 2025',
    documentType: 'ANNUAL_REPORT',
    description: 'Review of credit lines, interest subsidy disbursements, and SME refinancing performance across participating banks.',
    year: '2025',
    releaseDate: '2025-11-20',
    pdfEnglishUrl: '/documents/performance-report-2025-en.pdf',
    pdfEnglishSize: '4.1 MB',
    pdfSinhalaUrl: '/documents/performance-report-2025-si.pdf',
    pdfSinhalaSize: '4.4 MB',
    pdfTamilUrl: '/documents/performance-report-2025-ta.pdf',
    pdfTamilSize: '4.2 MB'
  },
  {
    id: 5,
    title: 'Annual Performance Report 2024',
    documentType: 'ANNUAL_REPORT',
    description: 'Annual evaluation of development financing programs, subsidized credit flows, and institutional portfolio health.',
    year: '2024',
    releaseDate: '2024-11-15',
    pdfEnglishUrl: '/documents/performance-report-2024-en.pdf',
    pdfEnglishSize: '3.9 MB',
    pdfSinhalaUrl: '/documents/performance-report-2024-si.pdf',
    pdfSinhalaSize: '4.0 MB'
  },
  {
    id: 6,
    title: 'MSME Concessionary Financing Circular 2026/01',
    documentType: 'CIRCULAR',
    description: 'Operational guidelines for commercial banks on interest subsidy claims and credit qualification.',
    year: '2026',
    releaseDate: '2026-02-01',
    pdfEnglishUrl: '/documents/circular-2026-01.pdf',
    pdfEnglishSize: '1.2 MB'
  }
];

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const selectYear = (year) => {
  selectedYear.value = year;
  isOpen.value = false;
};

const selectedYearLabel = computed(() => {
  return selectedYear.value === 'All' ? t('publications.allYears') : selectedYear.value;
});

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false;
  }
};

const fetchPublications = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const apiBase = BaseUrl.baseUrl || 'http://localhost:8096';
    const response = await axios.get(`${apiBase}/api/v1/publications`);
    if (Array.isArray(response.data) && response.data.length > 0) {
      publications.value = response.data;
    } else {
      publications.value = fallbackPublications;
    }
  } catch (err) {
    console.warn('Failed to fetch publications from backend, using fallback dataset:', err);
    publications.value = fallbackPublications;
  } finally {
    isLoading.value = false;
  }
};

// Available years for dropdown
const availableYears = computed(() => {
  const datasetYears = publications.value
    .map(p => String(p.year || '').trim())
    .filter(y => y.length > 0);

  const baseYears = ['2026', '2025', '2024'];
  const uniqueYears = Array.from(
    new Set([...baseYears, ...datasetYears])
  ).sort((a, b) => b.localeCompare(a));

  return ['All', ...uniqueYears];
});

const yearOptions = computed(() => {
  return availableYears.value.map(y => ({
    value: y,
    label: y === 'All' ? t('publications.allYears') : y
  }));
});

// Category counts across entire dataset
const allCount = computed(() => publications.value.length);
const totalActionPlansCount = computed(() => {
  return publications.value.filter(d => {
    const t = (d.documentType || d.category || '').toUpperCase();
    return t === 'ACTION_PLAN' || (!t && d.title?.toLowerCase().includes('action plan'));
  }).length;
});
const totalAnnualReportsCount = computed(() => {
  return publications.value.filter(d => {
    const t = (d.documentType || d.category || '').toUpperCase();
    return t === 'ANNUAL_REPORT' || (!t && (d.title?.toLowerCase().includes('annual') || d.title?.toLowerCase().includes('performance')));
  }).length;
});
const totalCircularsCount = computed(() => {
  return publications.value.filter(d => {
    const t = (d.documentType || d.category || '').toUpperCase();
    return t === 'CIRCULAR' || (!t && d.title?.toLowerCase().includes('circular'));
  }).length;
});

// Filter by Year, Search Query, and Category Tab
const filteredPublications = computed(() => {
  return publications.value.filter(doc => {
    const matchesYear = selectedYear.value === 'All' || String(doc.year) === selectedYear.value;
    const query = searchQuery.value.trim().toLowerCase();
    const matchesSearch = !query || 
      (doc.title && doc.title.toLowerCase().includes(query)) || 
      (doc.description && doc.description.toLowerCase().includes(query)) ||
      (doc.year && String(doc.year).includes(query));
    
    return matchesYear && matchesSearch;
  });
});

// Chronological sorting (Year DESC, Release Date DESC)
const sortByYearAndDate = (docs) => {
  return [...docs].sort((a, b) => {
    const yearA = parseInt(a.year) || 0;
    const yearB = parseInt(b.year) || 0;
    if (yearB !== yearA) return yearB - yearA;

    const dateA = a.releaseDate ? new Date(a.releaseDate).getTime() : 0;
    const dateB = b.releaseDate ? new Date(b.releaseDate).getTime() : 0;
    if (dateB !== dateA) return dateB - dateA;

    return (b.id || 0) - (a.id || 0);
  });
};

// Section 1: Action Plans
const actionPlans = computed(() => {
  const filtered = filteredPublications.value.filter(d => {
    const t = (d.documentType || d.category || '').toUpperCase();
    return t === 'ACTION_PLAN' || (!t && d.title?.toLowerCase().includes('action plan'));
  });
  return sortByYearAndDate(filtered);
});

// Section 2: Annual Performance Reports
const annualReports = computed(() => {
  const filtered = filteredPublications.value.filter(d => {
    const t = (d.documentType || d.category || '').toUpperCase();
    return t === 'ANNUAL_REPORT' || (!t && (d.title?.toLowerCase().includes('annual') || d.title?.toLowerCase().includes('performance')));
  });
  return sortByYearAndDate(filtered);
});

// Section 3: Circulars (Optional)
const circulars = computed(() => {
  const filtered = filteredPublications.value.filter(d => {
    const t = (d.documentType || d.category || '').toUpperCase();
    return t === 'CIRCULAR' || (!t && d.title?.toLowerCase().includes('circular'));
  });
  return sortByYearAndDate(filtered);
});

const showActionPlansSection = computed(() => {
  return selectedCategoryFilter.value === 'ALL' || selectedCategoryFilter.value === 'ACTION_PLAN';
});

const showAnnualReportsSection = computed(() => {
  return selectedCategoryFilter.value === 'ALL' || selectedCategoryFilter.value === 'ANNUAL_REPORT';
});

const showCircularsSection = computed(() => {
  return selectedCategoryFilter.value === 'ALL' || selectedCategoryFilter.value === 'CIRCULAR';
});

const hasAnyResults = computed(() => {
  let count = 0;
  if (showActionPlansSection.value) count += actionPlans.value.length;
  if (showAnnualReportsSection.value) count += annualReports.value.length;
  if (showCircularsSection.value) count += circulars.value.length;
  return count > 0;
});

// Helper: Available Languages for a Document
const getAvailableLanguages = (doc) => {
  if (!doc) return [];
  const list = [];
  
  if (doc.pdfEnglishUrl || doc.fileUrl) {
    list.push({
      key: 'en',
      code: 'EN',
      badgeLabel: 'EN',
      label: 'English Edition',
      nativeLabel: 'English',
      size: doc.pdfEnglishSize || doc.fileSize || 'PDF',
      url: doc.pdfEnglishUrl || doc.fileUrl
    });
  }
  if (doc.pdfSinhalaUrl) {
    list.push({
      key: 'si',
      code: 'SI',
      badgeLabel: 'සිංහල',
      label: 'Sinhala Edition',
      nativeLabel: 'සිංහල පිටපත',
      size: doc.pdfSinhalaSize || 'PDF',
      url: doc.pdfSinhalaUrl
    });
  }
  if (doc.pdfTamilUrl) {
    list.push({
      key: 'ta',
      code: 'TA',
      badgeLabel: 'தமிழ்',
      label: 'Tamil Edition',
      nativeLabel: 'தமிழ் பதிப்பு',
      size: doc.pdfTamilSize || 'PDF',
      url: doc.pdfTamilUrl
    });
  }

  // Fallback if none are populated but legacy url exists
  if (list.length === 0 && doc.url && doc.url !== '#') {
    list.push({
      key: 'en',
      code: 'EN',
      badgeLabel: 'EN',
      label: 'Official PDF Document',
      nativeLabel: 'PDF',
      size: doc.fileSize || 'PDF',
      url: doc.url
    });
  }

  return list;
};

// Modal helpers
const modalLanguages = computed(() => {
  return getAvailableLanguages(selectedDoc.value);
});

const openLanguageModal = (doc) => {
  selectedDoc.value = doc;
  showModal.value = true;
};

const closeLanguageModal = () => {
  showModal.value = false;
  selectedDoc.value = null;
};

const getMediaUrl = (url) => {
  if (!url || url === '#') return '#';
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  const apiBase = BaseUrl.baseUrl || 'http://localhost:8096';
  return `${apiBase}${url.startsWith('/') ? '' : '/'}${url}`;
};

const getDownloadUrl = (url) => {
  const full = getMediaUrl(url);
  if (full === '#') return '#';
  return full.includes('?') ? `${full}&download=true` : `${full}?download=true`;
};

const getFilenameForDownload = (doc, lang) => {
  const safeTitle = (doc.title || 'document').toLowerCase().replace(/[^a-z0-9]+/g, '-');
  return `${safeTitle}-${lang.code.toLowerCase()}.pdf`;
};

const formatDateDisplay = (dateStr, fallbackYear) => {
  if (!dateStr) return fallbackYear || 'N/A';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString('en-US', { day: 'numeric', month: 'short', year: 'numeric' });
};

const formatCategoryLabel = (category) => {
  const cat = (category || 'ACTION_PLAN').toUpperCase();
  if (cat === 'ANNUAL_REPORT') return 'Annual Performance Report';
  if (cat === 'CIRCULAR') return 'Circular / Policy';
  return 'Action Plan';
};

const getCategoryBadgeClass = (category) => {
  const cat = (category || 'ACTION_PLAN').toUpperCase();
  if (cat === 'ANNUAL_REPORT') return 'cat-badge-amber';
  if (cat === 'CIRCULAR') return 'cat-badge-indigo';
  return 'cat-badge-teal';
};

const resetFilters = () => {
  clearNavbarSearch();
  selectedYear.value = 'All';
  selectedCategoryFilter.value = 'ALL';
  isOpen.value = false;
};

onMounted(() => {
  if (route?.query?.q) {
    setNavbarSearchQuery(String(route.query.q));
  }
  fetchPublications();
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
  clearNavbarSearch();
});
</script>

<style scoped>
/* Page Layout */
.publications-page {
  background-color: #f8fafc;
  min-height: 100vh;
  padding-top: 90px;
  color: #1e293b;
}

.hero-header {
  background: linear-gradient(135deg, #004d5a 0%, #00797b 100%);
  padding: 3rem 1rem;
  border-bottom: 5px solid #00e5ff;
}

.hero-title {
  color: #ffffff;
  font-size: 2.3rem;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 0.5px;
}

.hero-subtitle {
  color: rgba(255, 255, 255, 0.88);
  font-size: 0.95rem;
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.6;
}

.pub-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 1.5rem;
  box-sizing: border-box;
}

/* Filter Bar */
.pub-filter-bar {
  background-color: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  padding: 0.85rem 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
  position: relative;
  z-index: 20;
}

.filter-flex-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}

.filter-tabs-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  overflow-x: auto;
  padding: 0.25rem 0;
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.filter-tabs-group::-webkit-scrollbar {
  display: none;
}

.filter-right-group {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  margin-left: auto;
}

.reset-filters-btn {
  background: transparent;
  border: none;
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  padding: 0.35rem 0.65rem;
  border-radius: 0.375rem;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
}
.reset-filters-btn:hover {
  color: #0f172a;
  background-color: #f1f5f9;
}

.filter-pill-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.42rem 0.95rem;
  border-radius: 9999px;
  font-size: 0.78rem;
  font-weight: 700;
  background-color: #f1f5f9;
  color: #475569;
  border: 1px solid transparent;
  transition: all 0.2s ease;
  white-space: nowrap;
  cursor: pointer;
}

.filter-pill-btn:hover {
  background-color: #e2e8f0;
  color: #1e293b;
}

.filter-pill-active {
  background-color: #00797b !important;
  color: #ffffff !important;
  box-shadow: 0 2px 6px rgba(0, 121, 123, 0.28);
}

.count-tag {
  padding: 0.1rem 0.45rem;
  border-radius: 9999px;
  font-size: 0.65rem;
  background: rgba(0, 0, 0, 0.08);
}

.filter-pill-active .count-tag {
  background: rgba(255, 255, 255, 0.25);
  color: #ffffff;
}

.filter-dropdown-area {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: #475569;
  letter-spacing: 0.05em;
  user-select: none;
}

.filter-icon {
  width: 14px;
  height: 14px;
  color: #00797b;
}

.custom-dropdown {
  position: relative;
  min-width: 130px;
}

.dropdown-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  background-color: #ffffff;
  border: 1px solid rgba(0, 121, 123, 0.35);
  color: #334155;
  font-size: 0.8rem;
  font-weight: 600;
  padding: 0.45rem 0.9rem;
  border-radius: 9999px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dropdown-btn:hover,
.dropdown-btn-open {
  border-color: #00797b;
  box-shadow: 0 0 0 3px rgba(0, 121, 123, 0.12);
}

.chevron-icon {
  width: 14px;
  height: 14px;
  color: #00797b;
  transition: transform 0.2s ease;
}

.rotate-180 {
  transform: rotate(180deg);
}

.dropdown-menu-list {
  position: absolute;
  right: 0;
  top: 100%;
  margin-top: 0.35rem;
  width: 100%;
  min-width: 140px;
  background-color: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 0.75rem;
  box-shadow: 0 12px 24px -4px rgba(15, 23, 42, 0.14);
  padding: 0.35rem;
  z-index: 50;
  overflow: hidden;
}

.dropdown-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: #475569;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 0.15s ease;
}

.dropdown-item:hover {
  background-color: rgba(0, 121, 123, 0.08);
  color: #00797b;
}

.item-selected {
  background-color: rgba(0, 121, 123, 0.12) !important;
  color: #00797b !important;
}

.item-label-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.active-dot {
  width: 5px;
  height: 5px;
  border-radius: 9999px;
  background-color: #00797b;
}

.check-icon {
  width: 14px;
  height: 14px;
  color: #00797b;
}

/* Main Content Area */
.pub-content-section {
  padding: 2.75rem 0 5rem 0;
}

.pub-type-section {
  margin-bottom: 3.5rem;
}

/* ITMD-Style Section Header with Teal Accent */
.pub-section-header {
  margin-bottom: 1.75rem;
  position: relative;
}

.pub-section-title-wrap {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  padding-bottom: 0.75rem;
}

.pub-section-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.pub-section-icon.teal {
  background-color: #f0fdfa;
  color: #0f766e;
  border: 1px solid #ccfbf1;
}

.pub-section-icon.amber {
  background-color: #fffbeb;
  color: #b45309;
  border: 1px solid #fef3c7;
}

.pub-section-icon.indigo {
  background-color: #eef2ff;
  color: #4338ca;
  border: 1px solid #e0e7ff;
}

.pub-section-headings {
  display: flex;
  flex-direction: column;
}

.pub-section-title-row {
  display: flex;
  align-items: center;
  gap: 0.65rem;
}

.pub-section-title {
  font-size: 1.35rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  line-height: 1.25;
  letter-spacing: -0.01em;
}

.pub-section-count {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.15rem 0.6rem;
  border-radius: 9999px;
}

.pub-section-count.teal {
  background: #f0fdfa;
  color: #0f766e;
  border: 1px solid #ccfbf1;
}

.pub-section-count.amber {
  background: #fffbeb;
  color: #b45309;
  border: 1px solid #fef3c7;
}

.pub-section-count.indigo {
  background: #eef2ff;
  color: #4338ca;
  border: 1px solid #e0e7ff;
}

.pub-section-desc {
  font-size: 0.8rem;
  color: #64748b;
  margin: 0.25rem 0 0 0;
}

.pub-section-accent-line {
  height: 3px;
  width: 100%;
  background: linear-gradient(to right, #00797b 90px, #e2e8f0 90px);
  border-radius: 2px;
}

.pub-section-accent-line.amber-line {
  background: linear-gradient(to right, #b45309 90px, #e2e8f0 90px);
}

.pub-section-accent-line.indigo-line {
  background: linear-gradient(to right, #4338ca 90px, #e2e8f0 90px);
}

/* ITMD 4-Column Responsive Grid */
.pub-card-grid {
  display: grid;
  grid-template-columns: repeat(1, minmax(0, 1fr));
  gap: 1.5rem;
  width: 100%;
}

@media (min-width: 640px) {
  .pub-card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 1024px) {
  .pub-card-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

/* Publication Card Design */
.pub-card {
  background-color: #ffffff;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px -1px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
  cursor: pointer;
  position: relative;
}

.pub-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px -3px rgba(0, 0, 0, 0.08), 0 4px 6px -4px rgba(0, 0, 0, 0.04);
  border-color: #cbd5e1;
}

/* Cover Preview Area */
.pub-card-cover {
  position: relative;
  width: 100%;
  height: 185px;
  background-color: #f1f5f9;
  overflow: hidden;
  border-bottom: 1px solid #f1f5f9;
}

.pub-card-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.pub-card:hover .pub-card-cover-img {
  transform: scale(1.05);
}

/* Branded Fallback Covers */
.branded-cover,
.branded-cover-annual,
.branded-cover-circular {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 1rem;
  position: relative;
  color: #ffffff;
  user-select: none;
}

.branded-cover {
  background: linear-gradient(145deg, #004d5a 0%, #00797b 100%);
}

.branded-cover-annual {
  background: linear-gradient(145deg, #78350f 0%, #b45309 100%);
}

.branded-cover-circular {
  background: linear-gradient(145deg, #312e81 0%, #4338ca 100%);
}

.cover-pattern-bg {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(255, 255, 255, 0.16) 1px, transparent 1px);
  background-size: 13px 13px;
  opacity: 0.55;
}

.cover-top-row {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cover-emblem-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.2rem 0.5rem;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(4px);
  font-size: 0.65rem;
  font-weight: 800;
  letter-spacing: 0.05em;
  color: #a5f3fc;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.cover-emblem-badge.emblem-amber {
  background: rgba(254, 240, 138, 0.2);
  color: #fef08a;
  border-color: rgba(254, 240, 138, 0.3);
}

.cover-emblem-badge.emblem-indigo {
  background: rgba(224, 231, 255, 0.2);
  color: #e0e7ff;
  border-color: rgba(224, 231, 255, 0.3);
}

.cover-year-badge {
  font-size: 0.95rem;
  font-weight: 900;
  color: #fef08a;
  letter-spacing: 0.05em;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.35);
}

.cover-year-badge.year-amber {
  color: #fed7aa;
}

.cover-year-badge.year-indigo {
  color: #c7d2fe;
}

.cover-center-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 0.25rem 0.5rem;
}

.cover-category-tag {
  font-size: 0.55rem;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #a5f3fc;
  margin: 0 0 0.3rem 0;
}

.cover-category-tag.tag-amber {
  color: #fed7aa;
}

.cover-category-tag.tag-indigo {
  color: #c7d2fe;
}

.cover-doc-title {
  font-size: 0.85rem;
  font-weight: 800;
  line-height: 1.35;
  color: #ffffff;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cover-footer-row {
  position: relative;
  z-index: 2;
  padding-top: 0.4rem;
  border-top: 1px solid rgba(255, 255, 255, 0.16);
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.65rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.75);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Hover overlay on cover */
.pub-cover-hover-overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(15, 23, 42, 0.35);
  opacity: 0;
  transition: opacity 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3;
}

.pub-card:hover .pub-cover-hover-overlay {
  opacity: 1;
}

.pub-cover-hover-pill {
  padding: 0.35rem 0.85rem;
  border-radius: 9999px;
  background-color: #ffffff;
  color: #0f172a;
  font-size: 0.72rem;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: scale(0.92);
  transition: transform 0.2s ease;
}

.pub-cover-hover-pill i {
  color: #00797b;
}

.pub-card:hover .pub-cover-hover-pill {
  transform: scale(1);
}

/* Card Body */
.pub-card-body {
  padding: 1.1rem;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  justify-content: space-between;
  background-color: #ffffff;
}

.pub-card-top-content {
  margin-bottom: 0.85rem;
}

.pub-card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  margin-bottom: 0.55rem;
}

.pub-card-date {
  font-size: 0.72rem;
  font-weight: 600;
  color: #64748b;
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
}

.pub-card-date i {
  color: #94a3b8;
}

.pub-lang-pills {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  flex-wrap: wrap;
}

.pub-lang-pill {
  font-size: 0.68rem;
  font-weight: 700;
  padding: 0.12rem 0.45rem;
  border-radius: 4px;
  line-height: 1.2;
  border-width: 1px;
  border-style: solid;
}

.pub-lang-pill.lang-en {
  background-color: #eff6ff;
  color: #1d4ed8;
  border-color: #bfdbfe;
}

.pub-lang-pill.lang-si {
  background-color: #ecfdf5;
  color: #047857;
  border-color: #a7f3d0;
}

.pub-lang-pill.lang-ta {
  background-color: #faf5ff;
  color: #7e22ce;
  border-color: #e9d5ff;
}

.pub-card-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
  margin: 0 0 0.5rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.pub-card:hover .pub-card-title {
  color: #00797b;
}

.pub-card-desc {
  font-size: 0.78rem;
  color: #64748b;
  line-height: 1.45;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Full-width "View / Download Document" Action Button */
.pub-card-action-btn {
  margin-top: auto;
  width: 100%;
  padding: 0.55rem 0.75rem;
  border-radius: 0.5rem;
  background-color: #f0fdfa;
  color: #0f766e;
  border: 1px solid #ccfbf1;
  font-weight: 600;
  font-size: 0.825rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  transition: all 0.2s ease;
  cursor: pointer;
  text-decoration: none;
}

.pub-card-action-btn:hover {
  background-color: #00797b;
  color: #ffffff;
  border-color: #00797b;
}

.pub-card-action-btn i {
  font-size: 0.8rem;
  transition: color 0.2s ease;
}

/* Empty State Card */
.empty-state-card {
  background-color: #ffffff;
  border-radius: 1rem;
  border: 1px solid #e2e8f0;
  padding: 4rem 1.5rem;
  text-align: center;
  max-width: 540px;
  margin: 2rem auto;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.empty-state-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background-color: #f1f5f9;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  margin: 0 auto 1.25rem auto;
}

.empty-state-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.35rem 0;
}

.empty-state-desc {
  font-size: 0.85rem;
  color: #64748b;
  max-width: 400px;
  margin: 0 auto 1.5rem auto;
  line-height: 1.5;
}

/* State Containers */
.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 1rem;
}

.loader-spinner {
  width: 42px;
  height: 42px;
  border: 3.5px solid rgba(0, 121, 123, 0.2);
  border-top-color: #00797b;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.state-text {
  font-size: 0.95rem;
  font-weight: 600;
  color: #64748b;
  margin: 0;
}

.state-icon-warning {
  font-size: 2.2rem;
  color: #f59e0b;
  margin-bottom: 1rem;
}

.btn-solid-teal {
  padding: 0.55rem 1.5rem;
  background-color: #00797b;
  color: #ffffff;
  border: none;
  border-radius: 9999px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 0.85rem;
}

.btn-solid-teal:hover {
  background-color: #005f61;
}

/* Multi-Language Selection Modal */
.pub-modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  background-color: rgba(15, 23, 42, 0.65);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  box-sizing: border-box;
}

.pub-modal-container {
  background-color: #ffffff;
  border-radius: 1rem;
  max-width: 540px;
  width: 100%;
  padding: 1.5rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  border: 1px solid #e2e8f0;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalPopIn 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modalPopIn {
  from {
    opacity: 0;
    transform: scale(0.96) translateY(8px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.pub-modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f1f5f9;
}

.pub-modal-header-left {
  flex-grow: 1;
  min-width: 0;
}

.pub-modal-badges {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.45rem;
}

.pub-modal-cat-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.55rem;
  border-radius: 4px;
  border-width: 1px;
  border-style: solid;
}

.cat-badge-teal {
  background-color: #f0fdfa;
  color: #0f766e;
  border-color: #ccfbf1;
}

.cat-badge-amber {
  background-color: #fffbeb;
  color: #b45309;
  border-color: #fef3c7;
}

.cat-badge-indigo {
  background-color: #eef2ff;
  color: #4338ca;
  border-color: #e0e7ff;
}

.pub-modal-year-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.55rem;
  border-radius: 4px;
  background-color: #f1f5f9;
  color: #334155;
}

.pub-modal-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.35;
  margin: 0;
}

.pub-modal-date {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0.35rem 0 0 0;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.pub-modal-date i {
  color: #94a3b8;
}

.pub-modal-close-btn {
  background: transparent;
  border: none;
  color: #94a3b8;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.pub-modal-close-btn:hover {
  background-color: #f1f5f9;
  color: #334155;
}

.pub-modal-desc-box {
  padding: 0.85rem 0;
  font-size: 0.8rem;
  color: #475569;
  line-height: 1.5;
  border-bottom: 1px solid #f1f5f9;
}

.pub-modal-languages-wrap {
  padding: 1.25rem 0 0.85rem 0;
}

.pub-modal-lang-heading {
  font-size: 0.72rem;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 0.85rem 0;
}

.pub-modal-lang-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.pub-modal-lang-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.85rem 1rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background-color: #f8fafc;
  transition: all 0.2s ease;
}

.pub-modal-lang-item:hover {
  background-color: #ffffff;
  border-color: #00797b;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.06);
}

.pub-modal-lang-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  min-width: 0;
}

.pub-modal-lang-badge {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 800;
  flex-shrink: 0;
  border-width: 1px;
  border-style: solid;
}

.pub-modal-lang-details {
  min-width: 0;
}

.pub-modal-lang-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pub-modal-lang-meta {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0.15rem 0 0 0;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.pub-modal-lang-native {
  color: #00797b;
  font-weight: 600;
}

.dot-sep {
  color: #cbd5e1;
}

.pub-modal-lang-actions {
  display: flex;
  align-items: center;
  gap: 0.45rem;
  flex-shrink: 0;
}

.pub-btn-view {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.42rem 0.85rem;
  border-radius: 0.5rem;
  background-color: #ffffff;
  color: #334155;
  border: 1px solid #cbd5e1;
  font-size: 0.75rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  cursor: pointer;
}

.pub-btn-view:hover {
  background-color: #f1f5f9;
  color: #00797b;
  border-color: #00797b;
}

.pub-btn-view i {
  color: #00797b;
}

.pub-btn-download {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.42rem 0.95rem;
  border-radius: 0.5rem;
  background-color: #00797b;
  color: #ffffff;
  border: 1px solid #00797b;
  font-size: 0.75rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  cursor: pointer;
}

.pub-btn-download:hover {
  background-color: #005f61;
  border-color: #005f61;
  color: #ffffff;
}

.pub-modal-footer {
  padding-top: 0.85rem;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
}

.pub-modal-btn-close {
  padding: 0.45rem 1.15rem;
  border-radius: 0.5rem;
  background-color: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}

.pub-modal-btn-close:hover {
  background-color: #e2e8f0;
  color: #1e293b;
}
</style>
