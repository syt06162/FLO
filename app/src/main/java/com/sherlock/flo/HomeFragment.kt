package com.sherlock.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.sherlock.flo.databinding.FragmentHomeBinding
import com.sherlock.flo.databinding.FragmentHomePanelBinding


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homePlot01Temp01ImageIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlbumFragment())
                .commitAllowingStateLoss()
        }

        // 최상단 메인 패널 (HomePanel)
        val panelAdapter = HomePanelViewpagerAdapter(this)
        panelAdapter.addFragment(HomePanelFragment(R.drawable.img_default_4_x_1, "운동할 때 듣는 신나는 댄스", 4, "2021.09.10",
            R.drawable.img_album_exp, "Butter", "BTS(방탄소년단)",
            R.drawable.img_album_exp2,"라일락(LILAC)", "아이유(IU)"))
        panelAdapter.addFragment(HomePanelFragment(R.drawable.img_default_4_x_5, "스트레스 해소에 딱 좋은 비트", 2, "2021.10.11",
            R.drawable.img_album_exp3, "Next Level", "aespa",
            R.drawable.img_album_exp4, "PERSONA", "BTS(방탄소년단)"))
        panelAdapter.addFragment(HomePanelFragment(R.drawable.img_default_4_x_3, "하루를 정리하며 일기 쓰기 좋은 뉴에이지", 4, "2022.01.04",
            R.drawable.img_album_exp5, "BAAM", "모모랜드",
            R.drawable.img_album_exp6,"Weekend", "태연"))
        binding.homePlot00PanelVp.adapter = panelAdapter
        binding.homePlot00PanelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homePlot00IndicatorIdct.setViewPager2(binding.homePlot00PanelVp)


        // 배너 광고 뷰페이저 만들기
        val bannerAdapter = BannerViewpagerAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homePlot01BannerVp.adapter = bannerAdapter
        binding.homePlot01BannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }


}