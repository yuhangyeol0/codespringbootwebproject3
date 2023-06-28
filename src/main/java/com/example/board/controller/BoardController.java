package com.example.board.controller;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageRequestDTO;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board/")
@Log4j2
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        log.info("list..........." + pageRequestDTO);

        model.addAttribute("result", boardService.getList(pageRequestDTO));

    }

    @GetMapping("/register")
    public void register(){
        log.info("register get....");
    }

    @PostMapping("/register")
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes){

        log.info("dto..." + dto);
        //새로 추가된 엔티티의 번호
        Long bno = boardService.register(dto);

        log.info("BNO: " + bno);

        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";
    }

    @GetMapping({"/read", "/modify"})
    public void read(@ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO, Long bno, Model model){
        log.info("bno : "+bno);
        BoardDTO boardDTO = boardService.get(bno);

        log.info(boardDTO);
        model.addAttribute("dto", boardDTO);
    }

    @PostMapping("/remove")
    public String remove(long bno, RedirectAttributes redirectAttributes){
        log.info("bno : " + bno);

        boardService.removeWithReplies(bno);

        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";
    }

    @PostMapping("/modify")
    public String modift(BoardDTO dto, @ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO,
                         RedirectAttributes redirectAttributes){
        log.info("post modify..................................");
        log.info("dto : ", dto);

        boardService.modify(dto);

        redirectAttributes.addFlashAttribute("page", pageRequestDTO.getPage());
        redirectAttributes.addFlashAttribute("type", pageRequestDTO.getType());
        redirectAttributes.addFlashAttribute("keyword", pageRequestDTO.getKeyword());
        redirectAttributes.addFlashAttribute("bno", dto.getBno());

        return "redirect:/board/read";
    }
}
